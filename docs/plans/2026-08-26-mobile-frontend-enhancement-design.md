# Yonke Fácil mobile frontend enhancement plan

## Goal

Make the Android staff app feel calmer, more legible, and more confidence-building for a third-age user while preserving the current workflow: receive vehicle → add part → search → sell. A new staff member should know where to tap without reading an instruction manual, and a mistake should be easy to notice and recover from.

## Recommended direction

Use an incremental **yard ledger** refresh. Keep the existing four-action home screen and Compose architecture, then improve the shared visual system and each screen in place.

Alternative directions considered:

1. **Dashboard-first:** add metrics, charts, and recent activity to the home screen. Rejected for v1 because it adds scanning work before the user can do the task.
2. **Wizard-first:** split every form into several guided steps. Rejected because it increases navigation and makes simple entries feel longer.
3. **Yard ledger refresh (recommended):** use strong section labels, large cards, paper-like surfaces, clear saved/offline states, and one unmistakable primary action per screen. It improves confidence without changing the mental model.

## Visual system

- **Navy ink `#17324D`:** headers, primary buttons, text hierarchy, and navigation.
- **Safety yellow `#F4C542`:** inventory tags, the receive-vehicle action, selected states, and confirmation cues.
- **Sky blue `#2C77B8`:** links, active navigation, search, and informational accents.
- **Paper `#F7F9FA`:** screen background; white cards remain the working surfaces.
- **Steel `#5F6B75`:** helper copy and secondary metadata.
- **Success green `#287A4B`:** saved, available, and synced states; error red stays reserved for destructive or invalid states.

Typography stays large and bilingual: bundle Atkinson Hyperlegible Next for instructions and form labels, and use Archivo Narrow for compact vehicle/reference identifiers (with a system-sans fallback if font loading is unavailable). Define a small type scale around 18–22sp body text, 24–32sp screen headings, and 40sp for the most important confirmation or login prompts. Use 56–64dp minimum controls, 12–16dp spacing, and consistent 10–12dp card corners.

Signature element: a **paper inventory tag** becomes a small yellow reference label used consistently on parts, sale confirmations, and owner recovery—not as decoration, but as the visual anchor for “which item am I changing?”

## Screen-by-screen plan

### 1. Login and phone connection

- Turn the header into a quiet brand strip with the language switch clearly separated.
- Give the staff cards a larger avatar block, a visible owner badge, and a full-card pressed state.
- Present the PIN screen as a focused “secure counter” state: selected staff name, four large PIN slots or a single large field, one primary **Entrar** button, and a clear **Atrás** escape route.
- Make enrollment feel like a short connection task with a visible 1–2–3 sequence: code, phone name, connect. Keep the copy plain and show exactly what the owner must provide.

### 2. Home

- Keep the four actions, but give them a stronger visual rhythm: a compact greeting/status row followed by four full-width action cards.
- Add a dynamic connection status strip (saved locally, waiting for internet, synced) instead of treating every state as a generic “Guardado.”
- Use distinct but restrained accent blocks for each action; keep the action title dominant and the helper sentence one line where possible.
- Move owner tools into a quieter administration section so staff never confuse it with daily work.

### 3. Receive vehicle

- Group fields into a single “Datos del vehículo” card with the required fields first: año, marca, modelo.
- Mark optional fields with a small “opcional” helper rather than relying on parentheses in every label.
- Add a short location example row with recent location suggestions when available, while retaining free text.
- Keep **Guardar vehículo** sticky at the bottom when the keyboard is closed; when the keyboard is open, let the form scroll naturally above it.
- Show validation next to the field that needs attention and preserve entered values after an error.

### 4. Add part

- Use distinct section cards: **Vehículo de origen**, **Nombre y categoría**, **Estado y precio**, **Foto y catálogo**.
- Replace the long wall of fields with clear bilingual pairs and a visible completion summary before saving.
- Treat condition as a large, horizontally scrollable choice row with selected color and text, never color alone.
- Show a real photo thumbnail after capture, with **Cambiar foto** as the secondary action.
- Make “Mostrar en el catálogo” read as a consequence: “Publicar esta pieza para clientes.” Keep the photo requirement adjacent to the switch.
- Keep the save button enabled only when the minimum data is valid, with a short reason when it is not.

### 5. Search inventory

- Make the search field the first visual object, with a large search icon, a clear button, and the reference example below it.
- Show the result count as a high-contrast status header and add quick filter chips for common makes/conditions.
- Upgrade `PartRow` into a compact inventory card: yellow reference tag, vehicle line, condition pill, location, price, and an obvious available/sold state.
- Keep the whole card tappable, but preserve a large, predictable touch target and TalkBack label.

### 6. Sell

- Lead with two equal paths: **Escanear etiqueta** and **Buscar por referencia**. This supports the fast path without punishing a user whose camera cannot read a label.
- Once a part is selected, show a confirmation sheet with the yellow reference tag, photo/placeholder, vehicle, location, and price before the destructive action.
- Make **Sí, vender** visually distinct from **Cancelar**, and make the resulting success state explicit.
- If the network is unavailable, explain that the sale is waiting for confirmation rather than implying it is complete.

### 7. Owner tools

- Divide the screen into labeled groups: **Etiquetas**, **Teléfonos**, **Personal**, and **Piezas vendidas**.
- Give enrollment codes a large, copyable presentation with expiry and a clear close action.
- Make sold history cards visually quieter than available inventory, with a clear **Restaurar** action and a confirmation step.

## Shared component work

Refactor `Components.kt` around a small set of reusable primitives: `ScreenHeader`, `StatusBanner`, `ActionCard`, `SectionCard`, `FieldLabel`, `InventoryTag`, `ConditionChip`, `PartRow`, `PrimaryButton`, and `SecondaryButton`. Centralize dimensions, colors, icon sizes, pressed/disabled states, and content descriptions in the theme/components layer. Keep screen copy in `Copy.kt`; do not put bilingual strings inside layout code.

## Accessibility and interaction guardrails

- Minimum 56dp touch targets; 64dp for primary actions and staff cards.
- Maintain readable contrast for every state and never communicate status by color alone.
- Preserve visible focus/pressed states and logical TalkBack order.
- Verify text scaling at 150% and 200%, portrait and landscape, and avoid clipping in Spanish.
- Add light haptic feedback only for successful save/sale or a completed scan; respect reduced-motion and accessibility settings.
- Keep destructive actions behind confirmation and make every network/offline state explicit.

## Implementation phases

1. **Foundation:** theme tokens, typography, shapes, elevation, icon sizing, button states, status banner, and reusable section/card primitives.
2. **First-run confidence:** login, enrollment, PIN, home, language toggle, and dynamic connection status.
3. **Daily work:** receive vehicle and add part, including field validation, photo preview, section cards, and save states.
4. **Find and sell:** search cards, quick filters, QR/manual sell entry, confirmation sheet, and offline wording.
5. **Owner and recovery:** grouped owner tools, enrollment code presentation, sold history, restore confirmation.
6. **Polish and verification:** Compose UI tests, text-scaling checks, physical Pixel QA, and refreshed demo APK.

## Verification checklist

- Login and PIN remain understandable without training.
- A user can reach each daily action from home in one tap.
- Required fields are obvious before a save attempt.
- Save, queued, failed, sold, and restored states are visually distinct and correctly worded in Spanish and English.
- No control is smaller than 56dp or clipped at 200% text size.
- Compose tests cover home actions, field validation, photo/publish gating, search filtering, sale confirmation, and owner restore.
- Install the release-like debug APK on the connected Pixel 6a and capture portrait screenshots for login, home, add part, search, sell, and owner recovery.

## Success criteria

After a short introduction, a staff member should be able to identify the right home action immediately, add a vehicle in under two minutes, add a photographed part in under one minute, sell a known part in under 30 seconds, and recover from an intentional wrong tap without developer help.
