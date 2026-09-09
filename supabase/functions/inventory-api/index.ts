import { createClient, type SupabaseClient } from "@supabase/supabase-js";
import { bodyOf, corsHeaders, error, functionPath, json, secret, sha256 } from "../_shared/http.ts";

type Actor = {
  businessId: string;
  ownerUserId: string | null;
  staffId: string | null;
  deviceId: string | null;
  role: "owner" | "staff";
  name: string;
};

type JsonRecord = Record<string, unknown>;

const supabaseUrl = Deno.env.get("SUPABASE_URL") ?? "";
const serviceKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";
const anonKey = Deno.env.get("SUPABASE_ANON_KEY") ?? "";
const service = createClient(supabaseUrl, serviceKey, { auth: { persistSession: false } });

async function deviceFromCredential(raw: string | null) {
  if (!raw) return null;
  const credentialHash = await sha256(raw);
  const { data } = await service.from("devices").select("id,business_id,name,is_active")
    .eq("credential_hash", credentialHash).eq("is_active", true).maybeSingle();
  if (data) await service.from("devices").update({ last_seen_at: new Date().toISOString() }).eq("id", data.id);
  return data;
}

async function actorFor(request: Request): Promise<Actor | null> {
  const authorization = request.headers.get("Authorization");
  if (authorization?.startsWith("Bearer ")) {
    const authClient = createClient(supabaseUrl, anonKey, {
      auth: { persistSession: false },
      global: { headers: { Authorization: authorization } },
    });
    const { data: auth } = await authClient.auth.getUser();
    if (auth.user) {
      const { data: profile } = await service.from("owner_profiles")
        .select("business_id,display_name").eq("user_id", auth.user.id).maybeSingle();
      if (profile) return {
        businessId: profile.business_id,
        ownerUserId: auth.user.id,
        staffId: null,
        deviceId: null,
        role: "owner",
        name: profile.display_name,
      };
    }
  }

  const rawSession = request.headers.get("X-Staff-Session");
  if (!rawSession) return null;
  const tokenHash = await sha256(rawSession);
  const { data: session } = await service.from("staff_sessions")
    .select("business_id,staff_id,device_id,expires_at")
    .eq("token_hash", tokenHash).gt("expires_at", new Date().toISOString()).maybeSingle();
  if (!session) return null;
  const { data: member } = await service.from("staff_members")
    .select("display_name,role,is_active").eq("id", session.staff_id).maybeSingle();
  if (!member?.is_active) return null;
  return {
    businessId: session.business_id,
    ownerUserId: null,
    staffId: session.staff_id,
    deviceId: session.device_id,
    role: member.role,
    name: member.display_name,
  };
}

async function cachedResult(businessId: string, requestId: string | null) {
  if (!requestId) return null;
  const { data } = await service.from("request_results").select("status_code,response_body")
    .eq("business_id", businessId).eq("request_id", requestId).maybeSingle();
  return data;
}

async function rememberResult(businessId: string, requestId: string, route: string, response: JsonRecord, statusCode: number) {
  await service.from("request_results").upsert({
    business_id: businessId,
    request_id: requestId,
    route,
    status_code: statusCode,
    response_body: response,
  }, { onConflict: "business_id,request_id", ignoreDuplicates: true });
}

async function audit(actor: Actor, entityType: string, entityId: string, action: string, before: unknown, after: unknown) {
  await service.from("audit_events").insert({
    business_id: actor.businessId,
    actor_staff_id: actor.staffId,
    actor_owner_id: actor.ownerUserId,
    device_id: actor.deviceId,
    entity_type: entityType,
    entity_id: entityId,
    action,
    before_data: before,
    after_data: after,
  });
}

function requireText(value: unknown, min = 1): value is string {
  return typeof value === "string" && value.trim().length >= min;
}

function requestIdOf(request: Request): string | null {
  const value = request.headers.get("X-Request-Id");
  return value && /^[0-9a-f-]{36}$/i.test(value) ? value : null;
}

async function listStaff(request: Request): Promise<Response> {
  const device = await deviceFromCredential(request.headers.get("X-Device-Credential"));
  if (!device) return error("device_required", "This phone is not enrolled.", 401);
  const { data, error: queryError } = await service.from("staff_members")
    .select("id,display_name,role").eq("business_id", device.business_id).eq("is_active", true).order("display_name");
  if (queryError) return error("staff_unavailable", "Staff could not be loaded.", 503);
  return json({ staff: data });
}

async function createEnrollment(request: Request, actor: Actor): Promise<Response> {
  if (actor.role !== "owner") return error("owner_required", "Owner access is required.", 403);
  const code = String(crypto.getRandomValues(new Uint32Array(1))[0] % 1_000_000).padStart(6, "0");
  const { error: insertError } = await service.from("device_enrollments").insert({
    business_id: actor.businessId,
    code_hash: await sha256(code),
    expires_at: new Date(Date.now() + 10 * 60_000).toISOString(),
    created_by: actor.ownerUserId,
    created_by_staff: actor.staffId,
  });
  if (insertError) return error("enrollment_failed", "Enrollment code could not be created.", 503);
  return json({ code, expiresInSeconds: 600 }, 201);
}

async function redeemEnrollment(request: Request): Promise<Response> {
  const body = await bodyOf<{ code?: string; deviceName?: string }>(request);
  if (!body || !/^\d{6}$/.test(body.code ?? "") || !requireText(body.deviceName, 2)) {
    return error("invalid_enrollment", "Enter the six-digit code and a phone name.", 422);
  }
  const codeHash = await sha256(body.code!);
  const { data: enrollment } = await service.from("device_enrollments").select("id,business_id,expires_at,redeemed_at")
    .eq("code_hash", codeHash).maybeSingle();
  if (!enrollment || enrollment.redeemed_at || new Date(enrollment.expires_at) <= new Date()) {
    return error("enrollment_expired", "This enrollment code is invalid or expired.", 410);
  }
  const credential = secret();
  const { data: device, error: insertError } = await service.from("devices").insert({
    business_id: enrollment.business_id,
    name: body.deviceName.trim(),
    credential_hash: await sha256(credential),
  }).select("id,name").single();
  if (insertError) return error("enrollment_failed", "This phone could not be enrolled.", 503);
  await service.from("device_enrollments").update({ redeemed_at: new Date().toISOString() }).eq("id", enrollment.id);
  return json({ device, credential }, 201);
}

async function createSession(request: Request): Promise<Response> {
  const device = await deviceFromCredential(request.headers.get("X-Device-Credential"));
  if (!device) return error("device_required", "This phone is not enrolled.", 401);
  const body = await bodyOf<{ staffId?: string; pin?: string }>(request);
  if (!body?.staffId || !/^\d{4}$/.test(body.pin ?? "")) return error("invalid_pin", "Enter a four-digit PIN.", 422);
  const { data, error: verifyError } = await service.rpc("verify_staff_pin", { p_staff_id: body.staffId, p_pin: body.pin });
  const member = data?.[0];
  if (verifyError || !member || member.business_id !== device.business_id) {
    return error("sign_in_failed", "The PIN is incorrect or temporarily locked.", 401);
  }
  const token = secret();
  const expiresAt = new Date(Date.now() + 12 * 60 * 60_000).toISOString();
  const { error: sessionError } = await service.from("staff_sessions").insert({
    business_id: member.business_id,
    staff_id: member.staff_id,
    device_id: device.id,
    token_hash: await sha256(token),
    expires_at: expiresAt,
  });
  if (sessionError) return error("sign_in_failed", "A session could not be created.", 503);
  return json({ token, expiresAt, staff: { id: member.staff_id, name: member.display_name, role: member.role } }, 201);
}

async function createStaff(request: Request, actor: Actor): Promise<Response> {
  if (actor.role !== "owner") return error("owner_required", "Owner access is required.", 403);
  const body = await bodyOf<{ displayName?: string; pin?: string; role?: "owner" | "staff" }>(request);
  if (!body || !requireText(body.displayName, 2) || !/^\d{4}$/.test(body.pin ?? "")) {
    return error("invalid_staff", "Name and a four-digit PIN are required.", 422);
  }
  const { data, error: createError } = await service.rpc("create_staff_member", {
    p_business_id: actor.businessId,
    p_display_name: body.displayName,
    p_pin: body.pin,
    p_role: body.role ?? "staff",
  });
  if (createError) return error("staff_create_failed", "Staff member could not be created.", 409);
  return json({ staff: { id: data.id, name: data.display_name, role: data.role } }, 201);
}

async function createVehicle(request: Request, actor: Actor): Promise<Response> {
  const requestId = requestIdOf(request);
  if (!requestId) return error("request_id_required", "X-Request-Id must be a UUID.", 422);
  const cached = await cachedResult(actor.businessId, requestId);
  if (cached) return json(cached.response_body, cached.status_code);
  const body = await bodyOf<{ year?: number; make?: string; model?: string; vin?: string; color?: string; locationNote?: string }>(request);
  if (!body || !Number.isInteger(body.year) || body.year! < 1900 || body.year! > 2100 || !requireText(body.make) || !requireText(body.model)) {
    return error("invalid_vehicle", "Year, make, and model are required.", 422);
  }
  const { data, error: insertError } = await service.from("vehicles").insert({
    business_id: actor.businessId,
    year: body.year,
    make: body.make.trim(),
    model: body.model.trim(),
    vin: body.vin?.trim() || null,
    color: body.color?.trim() || null,
    location_note: body.locationNote?.trim() || "",
    created_by: actor.staffId,
  }).select("*").single();
  if (insertError) return error("vehicle_create_failed", "Vehicle could not be saved.", 409);
  await audit(actor, "vehicle", data.id, "created", null, data);
  const response = { vehicle: data };
  await rememberResult(actor.businessId, requestId, "/vehicles", response, 201);
  return json(response, 201);
}

async function createPart(request: Request, actor: Actor): Promise<Response> {
  const requestId = requestIdOf(request);
  if (!requestId) return error("request_id_required", "X-Request-Id must be a UUID.", 422);
  const cached = await cachedResult(actor.businessId, requestId);
  if (cached) return json(cached.response_body, cached.status_code);
  const body = await bodyOf<{
    donorVehicleId?: string; nameEs?: string; nameEn?: string; categoryEs?: string; categoryEn?: string;
    aliases?: string[]; condition?: string; conditionNote?: string; locationNote?: string; priceMxnCents?: number | null;
    isPublished?: boolean; photo?: { storagePath: string; publicUrl: string };
  }>(request);
  if (!body || !body.donorVehicleId || !requireText(body.nameEs, 2) || !requireText(body.nameEn, 2)
    || !requireText(body.categoryEs, 2) || !requireText(body.categoryEn, 2) || !body.condition) {
    return error("invalid_part", "Vehicle, bilingual name/category, and condition are required.", 422);
  }
  if (body.isPublished && !body.photo) return error("photo_required", "A published part needs a photo.", 422);
  const { data: vehicle } = await service.from("vehicles").select("id").eq("id", body.donorVehicleId)
    .eq("business_id", actor.businessId).eq("status", "active").maybeSingle();
  if (!vehicle) return error("vehicle_not_found", "The donor vehicle was not found.", 404);
  const { data, error: insertError } = await service.from("parts").insert({
    business_id: actor.businessId,
    donor_vehicle_id: body.donorVehicleId,
    name_es: body.nameEs.trim(),
    name_en: body.nameEn.trim(),
    category_es: body.categoryEs.trim(),
    category_en: body.categoryEn.trim(),
    aliases: body.aliases ?? [],
    condition: body.condition,
    condition_note: body.conditionNote?.trim() || "",
    location_note: body.locationNote?.trim() || "",
    price_mxn_cents: body.priceMxnCents ?? null,
    is_published: body.isPublished ?? false,
    created_by: actor.staffId,
  }).select("*").single();
  if (insertError) return error("part_create_failed", "Part could not be saved.", 409);
  if (body.photo) await service.from("photos").insert({
    business_id: actor.businessId,
    part_id: data.id,
    storage_path: body.photo.storagePath,
    public_url: body.photo.publicUrl,
    alt_es: `${body.nameEs} ${data.reference}`,
    alt_en: `${body.nameEn} ${data.reference}`,
  });
  await audit(actor, "part", data.id, "created", null, data);
  const response = { part: data };
  await rememberResult(actor.businessId, requestId, "/parts", response, 201);
  return json(response, 201);
}

async function updateInventory(request: Request, actor: Actor, table: "vehicles" | "parts", id: string): Promise<Response> {
  const body = await bodyOf<{ revision?: number; changes?: JsonRecord }>(request);
  if (!body?.revision || !body.changes || typeof body.changes !== "object") return error("revision_required", "Revision and changes are required.", 422);
  const allowed = table === "vehicles"
    ? ["year", "make", "model", "vin", "color", "location_note", "status"]
    : ["name_es", "name_en", "category_es", "category_en", "aliases", "condition", "condition_note", "location_note", "price_mxn_cents", "is_published", "status"];
  const changes = Object.fromEntries(Object.entries(body.changes).filter(([key]) => allowed.includes(key)));
  if (!Object.keys(changes).length) return error("no_changes", "No editable fields were supplied.", 422);
  const { data: before } = await service.from(table).select("*").eq("id", id).eq("business_id", actor.businessId).maybeSingle();
  if (!before) return error("not_found", "Inventory record was not found.", 404);
  if (before.revision !== body.revision) return error("edit_conflict", "This record changed on another phone. Reload it before saving.", 409);
  const { data, error: updateError } = await service.from(table).update(changes).eq("id", id)
    .eq("business_id", actor.businessId).eq("revision", body.revision).select("*").maybeSingle();
  if (updateError || !data) return error("edit_conflict", "This record changed on another phone. Reload it before saving.", 409);
  await audit(actor, table === "parts" ? "part" : "vehicle", id, "updated", before, data);
  return json({ [table === "parts" ? "part" : "vehicle"]: data });
}

async function sellPart(request: Request, actor: Actor, id: string): Promise<Response> {
  const requestId = requestIdOf(request);
  if (!requestId) return error("request_id_required", "X-Request-Id must be a UUID.", 422);
  const cached = await cachedResult(actor.businessId, requestId);
  if (cached) return json(cached.response_body, cached.status_code);
  if (!actor.staffId) return error("staff_session_required", "Select a staff member before selling.", 403);
  const { data, error: sellError } = await service.rpc("sell_part", {
    p_part_id: id,
    p_business_id: actor.businessId,
    p_actor_staff_id: actor.staffId,
    p_device_id: actor.deviceId,
  });
  if (sellError) return error("part_not_available", "This part is no longer available.", 409);
  const response = { part: data };
  await rememberResult(actor.businessId, requestId, `/parts/${id}/sell`, response, 200);
  return json(response);
}

async function restorePart(actor: Actor, id: string): Promise<Response> {
  if (actor.role !== "owner") return error("owner_required", "Owner access is required.", 403);
  const operation = actor.ownerUserId
    ? service.rpc("restore_part", {
        p_part_id: id,
        p_business_id: actor.businessId,
        p_actor_owner_id: actor.ownerUserId,
      })
    : service.rpc("restore_part_as_staff_owner", {
        p_part_id: id,
        p_business_id: actor.businessId,
        p_actor_staff_id: actor.staffId,
        p_device_id: actor.deviceId,
      });
  const { data, error: restoreError } = await operation;
  if (restoreError) return error("restore_failed", "Only a sold part can be restored.", 409);
  return json({ part: data });
}

async function uploadUrl(actor: Actor): Promise<Response> {
  const path = `${actor.businessId}/${new Date().getUTCFullYear()}/${crypto.randomUUID()}.jpg`;
  const { data, error: uploadError } = await service.storage.from("inventory-photos").createSignedUploadUrl(path);
  if (uploadError) return error("upload_failed", "A photo upload could not be prepared.", 503);
  const publicUrl = service.storage.from("inventory-photos").getPublicUrl(path).data.publicUrl;
  return json({ storagePath: path, publicUrl, ...data }, 201);
}

async function listInventory(actor: Actor, table: "vehicles" | "parts", request: Request): Promise<Response> {
  const url = new URL(request.url);
  let query = service.from(table).select(table === "parts" ? "*,vehicles(year,make,model)" : "*")
    .eq("business_id", actor.businessId).order("updated_at", { ascending: false }).limit(250);
  if (url.searchParams.get("status")) query = query.eq("status", url.searchParams.get("status")!);
  const { data, error: queryError } = await query;
  if (queryError) return error("inventory_unavailable", "Inventory could not be loaded.", 503);
  return json({ [table]: data });
}

Deno.serve(async (request) => {
  if (request.method === "OPTIONS") return new Response(null, { status: 204, headers: corsHeaders });
  if (!supabaseUrl || !serviceKey || !anonKey) return error("server_config", "Inventory service is not configured.", 500);
  const path = functionPath(request, "inventory-api");

  if (request.method === "GET" && path === "/health") return json({ status: "ok" });
  if (request.method === "POST" && path === "/device-enrollments/redeem") return redeemEnrollment(request);
  if (request.method === "GET" && path === "/staff") return listStaff(request);
  if (request.method === "POST" && path === "/staff-sessions") return createSession(request);

  const actor = await actorFor(request);
  if (!actor) return error("authentication_required", "Sign in to continue.", 401);

  if (request.method === "POST" && path === "/device-enrollments") return createEnrollment(request, actor);
  if (request.method === "POST" && path === "/staff") return createStaff(request, actor);
  if (request.method === "POST" && path === "/uploads/sign") return uploadUrl(actor);
  if (request.method === "GET" && path === "/vehicles") return listInventory(actor, "vehicles", request);
  if (request.method === "POST" && path === "/vehicles") return createVehicle(request, actor);
  if (request.method === "GET" && path === "/parts") return listInventory(actor, "parts", request);
  if (request.method === "POST" && path === "/parts") return createPart(request, actor);

  const vehicleMatch = path.match(/^\/vehicles\/([0-9a-f-]+)$/i);
  if (request.method === "PATCH" && vehicleMatch) return updateInventory(request, actor, "vehicles", vehicleMatch[1]);
  const partMatch = path.match(/^\/parts\/([0-9a-f-]+)$/i);
  if (request.method === "PATCH" && partMatch) return updateInventory(request, actor, "parts", partMatch[1]);
  const sellMatch = path.match(/^\/parts\/([0-9a-f-]+)\/sell$/i);
  if (request.method === "POST" && sellMatch) return sellPart(request, actor, sellMatch[1]);
  const restoreMatch = path.match(/^\/parts\/([0-9a-f-]+)\/restore$/i);
  if (request.method === "POST" && restoreMatch) return restorePart(actor, restoreMatch[1]);

  if (request.method === "GET" && path === "/audit") {
    if (actor.role !== "owner") return error("owner_required", "Owner access is required.", 403);
    const { data } = await service.from("audit_events").select("*").eq("business_id", actor.businessId)
      .order("created_at", { ascending: false }).limit(100);
    return json({ events: data ?? [] });
  }
  return error("not_found", "API route not found.", 404);
});
