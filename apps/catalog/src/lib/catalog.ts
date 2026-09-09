import { demoCatalog } from "@/data/demo";
import type { CatalogPayload, PublicPart } from "@/lib/types";

const REQUEST_TIMEOUT_MS = 5_000;

function validPayload(value: unknown): value is CatalogPayload {
  if (!value || typeof value !== "object") return false;
  const payload = value as Partial<CatalogPayload>;
  return Array.isArray(payload.items) && Boolean(payload.contact?.name);
}

export async function getCatalog(): Promise<CatalogPayload> {
  const endpoint = import.meta.env.PUBLIC_API_URL?.trim();
  if (!endpoint) return demoCatalog;

  try {
    const response = await fetch(endpoint, {
      headers: { Accept: "application/json" },
      signal: AbortSignal.timeout(REQUEST_TIMEOUT_MS),
    });
    if (!response.ok) throw new Error(`Catalog responded ${response.status}`);
    const payload: unknown = await response.json();
    return validPayload(payload) ? payload : demoCatalog;
  } catch (error) {
    console.error("Catalog API unavailable; serving demo inventory", error);
    return demoCatalog;
  }
}

export async function getPart(id: string): Promise<{ payload: CatalogPayload; part: PublicPart | null }> {
  const payload = await getCatalog();
  return { payload, part: payload.items.find((item) => item.id === id) ?? null };
}

export function phoneHref(phoneE164: string): string {
  return `tel:+${phoneE164.replace(/\D/g, "")}`;
}

export function whatsappHref(phoneE164: string, message: string): string {
  const phone = phoneE164.replace(/\D/g, "");
  return `https://wa.me/${phone}?text=${encodeURIComponent(message)}`;
}

