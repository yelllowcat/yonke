export type Locale = "es" | "en";

export interface LocalizedText {
  es: string;
  en: string;
}

export interface PublicPart {
  id: string;
  reference: string;
  name: LocalizedText;
  category: LocalizedText;
  condition: LocalizedText;
  vehicle: {
    year: number;
    make: string;
    model: string;
    color?: string | null;
  };
  priceMxn: number | null;
  photoUrl: string | null;
  updatedAt: string;
}

export interface BusinessContact {
  name: string;
  phoneDisplay: string;
  phoneE164: string;
  whatsappE164: string;
  city: string;
  hours: LocalizedText;
}

export interface CatalogPayload {
  items: PublicPart[];
  contact: BusinessContact;
}

