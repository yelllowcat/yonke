import { useMemo, useState } from "preact/hooks";
import type { BusinessContact, Locale, PublicPart } from "@/lib/types";
import { whatsappHref } from "@/lib/catalog";

interface Props {
  items: PublicPart[];
  locale: Locale;
  contact: BusinessContact;
}

const copy = {
  es: {
    search: "¿Qué pieza busca?",
    example: "Ejemplo: alternador, Sentra 2012",
    quick: "Buscar por marca",
    year: "Todos los años",
    make: "Todas las marcas",
    tip: "También puede escribir el número de pieza.",
    clear: "Limpiar búsqueda",
    count: (value: number) => `${value} ${value === 1 ? "pieza disponible" : "piezas disponibles"}`,
    empty: "No encontramos esa pieza.",
    emptyHelp: "Pruebe otra palabra o pregúntenos por WhatsApp.",
    ask: "Preguntar precio",
    whatsapp: "Preguntar por WhatsApp",
    details: "Ver pieza",
    available: "Disponible hoy",
    currency: "MXN",
  },
  en: {
    search: "What part do you need?",
    example: "Example: alternator, 2012 Sentra",
    quick: "Browse by make",
    year: "All years",
    make: "All makes",
    tip: "You can also type the part number.",
    clear: "Clear search",
    count: (value: number) => `${value} ${value === 1 ? "part available" : "parts available"}`,
    empty: "We couldn't find that part.",
    emptyHelp: "Try another word or ask us on WhatsApp.",
    ask: "Ask for price",
    whatsapp: "Ask on WhatsApp",
    details: "View part",
    available: "Available today",
    currency: "MXN",
  },
};

const normalize = (value: string) =>
  value.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLocaleLowerCase();

export default function CatalogExplorer({ items, locale, contact }: Props) {
  const labels = copy[locale];
  const [query, setQuery] = useState("");
  const [year, setYear] = useState("");
  const [make, setMake] = useState("");

  const years = [...new Set(items.map((item) => item.vehicle.year))].sort((a, b) => b - a);
  const makes = [...new Set(items.map((item) => item.vehicle.make))].sort();

  const filtered = useMemo(() => {
    const needle = normalize(query.trim());
    return items.filter((item) => {
      const searchable = normalize([
        item.name.es,
        item.name.en,
        item.category.es,
        item.category.en,
        item.vehicle.year,
        item.vehicle.make,
        item.vehicle.model,
        item.reference,
      ].join(" "));
      return (!needle || searchable.includes(needle))
        && (!year || item.vehicle.year === Number(year))
        && (!make || item.vehicle.make === make);
    });
  }, [items, make, query, year]);

  return (
    <section class="catalog" aria-labelledby="results-heading">
      <div class="search-panel" role="search">
        <div class="search-heading">
          <label class="search-label" for="part-search">{labels.search}</label>
          <span class="search-step">01</span>
        </div>
        <div class="search-input-wrap">
          <span class="search-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" focusable="false"><circle cx="10.8" cy="10.8" r="6.8" /><path d="m16 16 5 5" /></svg>
          </span>
          <input
            id="part-search"
            type="search"
            value={query}
            onInput={(event) => setQuery(event.currentTarget.value)}
            placeholder={labels.example}
            autocomplete="off"
          />
          {query && (
            <button class="clear-input" type="button" aria-label={labels.clear} onClick={() => setQuery("")}>
              ×
            </button>
          )}
        </div>
        <div class="filters">
          <label>
            <span class="visually-hidden">{labels.year}</span>
            <select value={year} onChange={(event) => setYear(event.currentTarget.value)}>
              <option value="">{labels.year}</option>
              {years.map((item) => <option value={item}>{item}</option>)}
            </select>
          </label>
          <label>
            <span class="visually-hidden">{labels.make}</span>
            <select value={make} onChange={(event) => setMake(event.currentTarget.value)}>
              <option value="">{labels.make}</option>
              {makes.map((item) => <option value={item}>{item}</option>)}
            </select>
          </label>
        </div>
        <div class="search-footer">
          <span>{labels.tip}</span>
          {(query || year || make) && (
            <button class="clear-button" type="button" onClick={() => { setQuery(""); setYear(""); setMake(""); }}>
              {labels.clear}
            </button>
          )}
        </div>
        {makes.length > 1 && (
          <div class="quick-searches" aria-label={labels.quick}>
            <span>{labels.quick}</span>
            <div>
              {makes.slice(0, 4).map((item) => (
                <button
                  class={make === item ? "quick-filter is-active" : "quick-filter"}
                  type="button"
                  aria-pressed={make === item}
                  onClick={() => setMake(make === item ? "" : item)}
                >
                  {item}
                </button>
              ))}
            </div>
          </div>
        )}
      </div>

      <div class="results-heading">
        <div>
          <p class="section-kicker">02 · {locale === "es" ? "Inventario" : "Inventory"}</p>
          <h2 id="results-heading" aria-live="polite">{labels.count(filtered.length)}</h2>
        </div>
        <span class="availability-stamp"><i aria-hidden="true" />{labels.available}</span>
      </div>

      {filtered.length ? (
        <div class="part-grid">
          {filtered.map((part) => {
            const vehicle = `${part.vehicle.year} ${part.vehicle.make} ${part.vehicle.model}`;
            const message = locale === "es"
              ? `Hola, pregunto por ${part.name.es} ${vehicle}, referencia ${part.reference}. ¿Sigue disponible?`
              : `Hello, I'm asking about ${part.name.en} for a ${vehicle}, reference ${part.reference}. Is it still available?`;
            const detailHref = locale === "es" ? `/parte/${part.id}` : `/en/part/${part.id}`;
            return (
              <article class="part-card" key={part.id}>
                <div class="part-photo">
                  {part.photoUrl ? (
                    <img src={part.photoUrl} alt={`${part.name[locale]} · ${vehicle}`} loading="lazy" />
                  ) : (
                    <div class="photo-placeholder" aria-label={locale === "es" ? "Foto pendiente" : "Photo pending"}>
                      <svg viewBox="0 0 120 74" aria-hidden="true">
                        <path d="M18 47h84l-8-22H31L18 47Zm7 0v11m70-11v11M38 25l8-10h30l9 10" />
                        <circle cx="37" cy="48" r="7" /><circle cx="84" cy="48" r="7" />
                      </svg>
                    </div>
                  )}
                  <span class="condition">{part.condition[locale]}</span>
                </div>
                <div class="part-body">
                  <div class="card-topline">
                    <div class="inventory-tag"><span>{part.reference}</span></div>
                    <span class="card-availability">{labels.available}</span>
                  </div>
                  <p class="vehicle">{vehicle}</p>
                  <h3><a href={detailHref}>{part.name[locale]}</a></h3>
                  <p class="category">{part.category[locale]}</p>
                  <div class="price-row">
                    {part.priceMxn == null ? (
                      <strong>{labels.ask}</strong>
                    ) : (
                      <strong>${part.priceMxn.toLocaleString(locale === "es" ? "es-MX" : "en-US")} <small>{labels.currency}</small></strong>
                    )}
                  </div>
                  <div class="card-actions">
                    <a class="button button-primary" href={whatsappHref(contact.whatsappE164, message)} target="_blank" rel="noreferrer">
                      {labels.whatsapp}
                    </a>
                    <a class="button button-quiet" href={detailHref}>{labels.details}</a>
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      ) : (
        <div class="empty-state" role="status">
          <span aria-hidden="true">?</span>
          <h3>{labels.empty}</h3>
          <p>{labels.emptyHelp}</p>
          <a class="button button-primary" href={whatsappHref(contact.whatsappE164, labels.empty)} target="_blank" rel="noreferrer">
            WhatsApp
          </a>
        </div>
      )}
    </section>
  );
}
