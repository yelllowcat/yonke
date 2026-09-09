import type { CatalogPayload } from "@/lib/types";

export const demoCatalog: CatalogPayload = {
  contact: {
    name: "Yonke Fácil",
    phoneDisplay: "669 123 4567",
    phoneE164: "526691234567",
    whatsappE164: "526691234567",
    city: "Mazatlán, Sinaloa",
    hours: {
      es: "Lun–Sáb · 8:00–18:00",
      en: "Mon–Sat · 8:00–18:00",
    },
  },
  items: [
    {
      id: "4cf2e131-71ea-4c96-8e09-8d48f5a82201",
      reference: "P-1042",
      name: { es: "Alternador", en: "Alternator" },
      category: { es: "Sistema eléctrico", en: "Electrical" },
      condition: { es: "Probado", en: "Tested" },
      vehicle: { year: 2012, make: "Nissan", model: "Sentra", color: "Plata" },
      priceMxn: 1450,
      photoUrl: null,
      updatedAt: "2026-08-26T16:00:00Z",
    },
    {
      id: "4cf2e131-71ea-4c96-8e09-8d48f5a82202",
      reference: "P-1047",
      name: { es: "Faro delantero izquierdo", en: "Left headlight" },
      category: { es: "Carrocería", en: "Body" },
      condition: { es: "Buen estado", en: "Good condition" },
      vehicle: { year: 2015, make: "Chevrolet", model: "Aveo", color: "Blanco" },
      priceMxn: 900,
      photoUrl: null,
      updatedAt: "2026-08-25T18:20:00Z",
    },
    {
      id: "4cf2e131-71ea-4c96-8e09-8d48f5a82203",
      reference: "P-1051",
      name: { es: "Puerta trasera derecha", en: "Right rear door" },
      category: { es: "Carrocería", en: "Body" },
      condition: { es: "Con detalle", en: "Cosmetic wear" },
      vehicle: { year: 2018, make: "Kia", model: "Rio", color: "Rojo" },
      priceMxn: null,
      photoUrl: null,
      updatedAt: "2026-08-24T13:10:00Z",
    },
    {
      id: "4cf2e131-71ea-4c96-8e09-8d48f5a82204",
      reference: "P-1058",
      name: { es: "Compresor de aire", en: "A/C compressor" },
      category: { es: "Clima", en: "Climate" },
      condition: { es: "Sin probar", en: "Untested" },
      vehicle: { year: 2014, make: "Ford", model: "Fiesta", color: "Azul" },
      priceMxn: 1200,
      photoUrl: null,
      updatedAt: "2026-08-23T17:45:00Z",
    },
  ],
};

