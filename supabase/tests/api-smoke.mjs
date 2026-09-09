import assert from "node:assert/strict";
import { randomUUID } from "node:crypto";

const base = process.env.INVENTORY_API_URL ?? "http://127.0.0.1:54321/functions/v1/inventory-api";
const deviceCredential = process.env.DEVICE_CREDENTIAL ?? "local-device-credential";

async function request(path, options = {}) {
  const response = await fetch(`${base}${path}`, options);
  const body = await response.json();
  if (!response.ok) throw new Error(`${options.method ?? "GET"} ${path}: ${response.status} ${JSON.stringify(body)}`);
  return { response, body };
}

const staffResponse = await request("/staff", {
  headers: { "X-Device-Credential": deviceCredential },
});
const ana = staffResponse.body.staff.find((member) => member.display_name === "Ana");
assert.ok(ana, "seeded staff is discoverable on an enrolled phone");

const sessionResponse = await request("/staff-sessions", {
  method: "POST",
  headers: { "Content-Type": "application/json", "X-Device-Credential": deviceCredential },
  body: JSON.stringify({ staffId: ana.id, pin: "1234" }),
});
assert.equal(sessionResponse.response.status, 201);
const session = sessionResponse.body.token;
assert.ok(session);

const vehicleRequestId = randomUUID();
const vehicleOptions = {
  method: "POST",
  headers: { "Content-Type": "application/json", "X-Staff-Session": session, "X-Request-Id": vehicleRequestId },
  body: JSON.stringify({ year: 2020, make: "Toyota", model: "Corolla", color: "Gris", locationNote: "Junto al portón" }),
};
const created = await request("/vehicles", vehicleOptions);
const repeated = await request("/vehicles", vehicleOptions);
assert.equal(created.response.status, 201);
assert.equal(repeated.body.vehicle.id, created.body.vehicle.id, "repeated request returns the original vehicle");

const saleRequestId = randomUUID();
const saleOptions = {
  method: "POST",
  headers: { "Content-Type": "application/json", "X-Staff-Session": session, "X-Request-Id": saleRequestId },
  body: "{}",
};
const sold = await request("/parts/4cf2e131-71ea-4c96-8e09-8d48f5a82201/sell", saleOptions);
const repeatedSale = await request("/parts/4cf2e131-71ea-4c96-8e09-8d48f5a82201/sell", saleOptions);
assert.equal(sold.body.part.status, "sold");
assert.equal(repeatedSale.body.part.id, sold.body.part.id, "repeated sale returns the original result");

const david = staffResponse.body.staff.find((member) => member.display_name === "David");
const ownerSessionResponse = await request("/staff-sessions", {
  method: "POST",
  headers: { "Content-Type": "application/json", "X-Device-Credential": deviceCredential },
  body: JSON.stringify({ staffId: david.id, pin: "2468" }),
});
const ownerSession = ownerSessionResponse.body.token;
const restored = await request("/parts/4cf2e131-71ea-4c96-8e09-8d48f5a82201/restore", {
  method: "POST",
  headers: { "Content-Type": "application/json", "X-Staff-Session": ownerSession },
  body: "{}",
});
assert.equal(restored.body.part.status, "available", "PIN-authenticated owner can recover a sale");
const enrollment = await request("/device-enrollments", {
  method: "POST",
  headers: { "Content-Type": "application/json", "X-Staff-Session": ownerSession },
  body: "{}",
});
assert.match(enrollment.body.code, /^\d{6}$/, "owner can create a phone enrollment code");

console.log(JSON.stringify({
  staffLogin: "ok",
  vehicleIdempotency: "ok",
  atomicSale: "ok",
  ownerRecovery: "ok",
  phoneEnrollment: "ok",
  vehicleReference: created.body.vehicle.reference,
  partReference: sold.body.part.reference,
}));
