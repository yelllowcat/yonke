import { cleanup, fireEvent, render, screen } from "@testing-library/preact";
import { afterEach, describe, expect, it } from "vitest";
import { demoCatalog } from "@/data/demo";
import CatalogExplorer from "./CatalogExplorer";

afterEach(cleanup);

describe("CatalogExplorer", () => {
  it("finds a part with plain bilingual search", () => {
    render(<CatalogExplorer items={demoCatalog.items} locale="es" contact={demoCatalog.contact} />);
    fireEvent.input(screen.getByRole("searchbox", { name: "¿Qué pieza busca?" }), {
      target: { value: "Sentra" },
    });
    expect(screen.getByRole("heading", { name: "1 pieza disponible" })).toBeDefined();
    expect(screen.getByRole("link", { name: "Alternador" })).toBeDefined();
    expect(screen.queryByRole("link", { name: "Faro delantero izquierdo" })).toBeNull();
  });

  it("matches English aliases from the Spanish interface", () => {
    render(<CatalogExplorer items={demoCatalog.items} locale="es" contact={demoCatalog.contact} />);
    fireEvent.input(screen.getByRole("searchbox", { name: "¿Qué pieza busca?" }), {
      target: { value: "headlight" },
    });
    expect(screen.getByRole("link", { name: "Faro delantero izquierdo" })).toBeDefined();
  });
});
