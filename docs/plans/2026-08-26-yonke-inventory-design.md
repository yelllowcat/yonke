# Yonke Fácil: validated product design

## Product

Yonke Fácil serves one Mexican auto-salvage yard with a native Android staff app and an Astro public catalog. The operating workflow is receive a donor vehicle, register removed parts, find inventory, and mark a part sold. Customers browse published inventory and call or send a prefilled WhatsApp message; they do not create accounts, reserve, or pay online.

## Staff experience

The home screen exposes four large actions: receive vehicle, add part, search, and sell. Screens use icon-plus-text controls, high contrast, dynamic text, one decision per screen, confirmations, and owner recovery. Spanish and English are first-class. Manual vehicle entry, recent free-text locations, voice dictation, photos, and optional QR labels reduce memory and typing demands.

The app is cloud-first. Drafts survive restart, creation requests retry idempotently after brief outages, and final sale or recovery requires server confirmation.

## Data and security

Supabase holds business settings, staff, devices, donor vehicles, parts, photos, idempotency records, and immutable audit events. Public responses are projections that omit VINs, internal locations, staff identities, unpublished/sold inventory, and audit history. Owner and staff permissions are enforced in the API and database, not only in the UI.

## Visual direction

The interface is a calm yard ledger: navy structure, safety-yellow actions and inventory tags, sky-blue navigation, white working surfaces, and steel-gray supporting copy. Atkinson Hyperlegible is used for customer-facing body copy and Archivo Narrow for compact vehicle identifiers. Decorative rust and distressed textures are intentionally excluded because they reduce legibility.

## Acceptance

After a 15-minute introduction, an intended user should add a vehicle within two minutes, add a part within one minute, locate and sell a known part within 30 seconds, and recover from an intentional mistake without developer help. Production follows a fake-data prototype, a one-week shadow pilot, and verified backup/recovery.

