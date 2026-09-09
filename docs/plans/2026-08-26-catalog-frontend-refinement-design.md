# Yonke Fácil catalog frontend refinement

## Design intent

Make the public inventory catalog feel like a clear, trustworthy counter window at a real yonke: a visitor should immediately understand what is available, search without learning the interface, and know exactly how to ask about a part.

## Direction

- **Palette:** navy ink (`#17324d`) for structure, safety yellow (`#f4c542`) for inventory and action cues, sky blue (`#2c77b8`) for navigation, paper white (`#f7f9fa`) for the working surface, steel (`#5f6b75`) for secondary copy, and yard green (`#287a4b`) for available/success states.
- **Type:** Atkinson Hyperlegible Next remains the reading face; Archivo Narrow remains the compact, industrial identifier face.
- **Layout:** a strong counter-window header and hero, an overlapping search desk, then a simple inventory grid. Search helpers and status labels are placed where a person scanning a physical tag would look first.
- **Signature:** each part card gets a quiet yellow “tag rail” and a clipped reference label, echoing the paper inventory tags used in a yard without adding distressed texture or visual noise.

## Accessibility guardrails

Keep the large touch targets, visible focus rings, plain verbs, and high-contrast text. Motion is limited to short card lift/focus transitions and respects `prefers-reduced-motion`. The search remains a single field with optional filters, and all quick filters have text labels and keyboard focus.
