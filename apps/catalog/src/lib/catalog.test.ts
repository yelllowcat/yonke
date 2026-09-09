import { describe, expect, it } from "vitest";
import { phoneHref, whatsappHref } from "./catalog";

describe("contact links", () => {
  it("normalizes a phone link", () => {
    expect(phoneHref("+52 (669) 123-4567")).toBe("tel:+526691234567");
  });

  it("encodes a WhatsApp message", () => {
    expect(whatsappHref("+52 669", "¿Sigue disponible?")).toBe(
      "https://wa.me/52669?text=%C2%BFSigue%20disponible%3F",
    );
  });
});

