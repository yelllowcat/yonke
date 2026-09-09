import { createClient } from "@supabase/supabase-js";
import { corsHeaders, error, json } from "../_shared/http.ts";

const conditionCopy: Record<string, { es: string; en: string }> = {
  tested: { es: "Probado", en: "Tested" },
  good: { es: "Buen estado", en: "Good condition" },
  untested: { es: "Sin probar", en: "Untested" },
  cosmetic_wear: { es: "Con detalle", en: "Cosmetic wear" },
  damaged: { es: "Dañado", en: "Damaged" },
};

Deno.serve(async (request) => {
  if (request.method === "OPTIONS") return new Response(null, { status: 204, headers: corsHeaders });
  if (request.method !== "GET") return error("method_not_allowed", "Use GET.", 405);

  const url = Deno.env.get("SUPABASE_URL");
  const serviceKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
  if (!url || !serviceKey) return error("server_config", "Catalog service is not configured.", 500);

  const requestUrl = new URL(request.url);
  const businessSlug = requestUrl.searchParams.get("business") ?? "yonke-facil";
  const query = requestUrl.searchParams.get("q")?.trim().toLocaleLowerCase() ?? "";
  const client = createClient(url, serviceKey, { auth: { persistSession: false } });
  const { data, error: queryError } = await client
    .from("public_catalog_parts")
    .select("*")
    .eq("business_slug", businessSlug)
    .order("updated_at", { ascending: false })
    .limit(250);

  if (queryError) {
    console.error(queryError);
    return error("catalog_unavailable", "Inventory could not be loaded.", 503);
  }
  if (!data?.length) return error("catalog_not_found", "No public inventory is configured.", 404);

  const normalized = query.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  const rows = normalized ? data.filter((row) => {
    const text = [
      row.name_es, row.name_en, row.category_es, row.category_en,
      ...(row.aliases ?? []), row.year, row.make, row.model, row.reference,
    ].join(" ").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLocaleLowerCase();
    return text.includes(normalized);
  }) : data;
  const first = data[0];

  return json({
    contact: {
      name: first.business_name,
      phoneDisplay: first.phone_display,
      phoneE164: first.phone_e164,
      whatsappE164: first.whatsapp_e164,
      city: first.city,
      hours: { es: first.hours_es, en: first.hours_en },
    },
    items: rows.map((row) => ({
      id: row.id,
      reference: row.reference,
      name: { es: row.name_es, en: row.name_en },
      category: { es: row.category_es, en: row.category_en },
      condition: conditionCopy[row.condition] ?? conditionCopy.untested,
      vehicle: { year: row.year, make: row.make, model: row.model, color: row.color },
      priceMxn: row.price_mxn_cents == null ? null : row.price_mxn_cents / 100,
      photoUrl: row.photo_url,
      updatedAt: row.updated_at,
    })),
  });
});

