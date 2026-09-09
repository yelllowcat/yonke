export const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, apikey, content-type, x-device-credential, x-staff-session, x-request-id",
  "Access-Control-Allow-Methods": "GET, POST, PATCH, OPTIONS",
};

export function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: {
      ...corsHeaders,
      "Content-Type": "application/json; charset=utf-8",
      "Cache-Control": status === 200 ? "no-store" : "no-store",
    },
  });
}

export function error(code: string, message: string, status = 400): Response {
  return json({ error: { code, message } }, status);
}

export async function sha256(value: string): Promise<string> {
  const data = new TextEncoder().encode(value);
  const digest = await crypto.subtle.digest("SHA-256", data);
  return Array.from(new Uint8Array(digest)).map((byte) => byte.toString(16).padStart(2, "0")).join("");
}

export function secret(bytes = 32): string {
  const value = crypto.getRandomValues(new Uint8Array(bytes));
  return Array.from(value).map((byte) => byte.toString(16).padStart(2, "0")).join("");
}

export function functionPath(request: Request, functionName: string): string {
  const pathname = new URL(request.url).pathname;
  const marker = `/${functionName}`;
  const offset = pathname.indexOf(marker);
  if (offset === -1) return "/";
  return pathname.slice(offset + marker.length) || "/";
}

export async function bodyOf<T>(request: Request): Promise<T | null> {
  try {
    return await request.json() as T;
  } catch {
    return null;
  }
}

