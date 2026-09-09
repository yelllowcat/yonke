# Yonke Fácil

Inventory management for a Mexican auto-salvage yard, designed for staff who need large controls, plain language, and minimal typing.

The repository contains:

- `android/`: native Kotlin + Jetpack Compose staff application.
- `apps/catalog/`: Astro customer catalog in Spanish and English.
- `supabase/`: PostgreSQL schema, seed data, Row Level Security, and Edge Functions.
- `docs/plans/`: the validated product design and operating assumptions.

## Quick start: customer catalog

```bash
npm install
npm run dev
```

The catalog starts with safe demo data when `PUBLIC_API_URL` is not configured. Copy `apps/catalog/.env.example` to `apps/catalog/.env` and set the API URL to use a deployed Supabase catalog function.

## Quick start: backend

Docker must be running.

```bash
npm install
npx supabase start
npx supabase db reset
npx supabase functions serve catalog --no-verify-jwt
npx supabase functions serve inventory-api --no-verify-jwt
```

Local API URLs are shown by `npx supabase status`. Never place the service-role key in the Astro site or Android build.

## Quick start: Android

Open `android/` in Android Studio, allow it to create `local.properties`, and run the `app` configuration on Android 10 or newer. From a terminal with `ANDROID_HOME` configured:

```bash
cd android
./gradlew test assembleDebug
```

The app uses an in-memory demo repository until `API_BASE_URL` is supplied as a Gradle property. With an Android emulator and local Supabase:

```bash
./gradlew installDebug -PAPI_BASE_URL=http://10.0.2.2:54321/functions/v1/inventory-api
```

On first launch in network mode, enter a six-digit phone-enrollment code created from owner tools on an already enrolled phone. Network mode connects device enrollment, PIN sessions, inventory refresh, signed photo upload, queued vehicle intake, atomic sales, owner recovery, staff creation, and enrollment-code generation to the Edge API. Debug builds permit local cleartext HTTP; release builds require HTTPS.

## Demo access

- Staff: `Ana`, PIN `1234`
- Owner: `David`, PIN `2468`

The local backend seed uses the same staff PINs for smoke testing. Seed data must never be applied to production.
