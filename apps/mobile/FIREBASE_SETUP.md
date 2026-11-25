# Configuración de Firebase para Yonke Fácil (Flutter)

La app en `apps/mobile` ya está conectada a **Cloud Firestore** (con caché offline activada y transacciones atómicas para ventas) y **Firebase Storage** (subida real de fotos capturadas con la cámara).

Mientras no conectes tu proyecto de Firebase, la app inicia automáticamente en modo local de demostración sin errores.

## Conectar tu proyecto Firebase en 3 pasos

1. **Inicia sesión en Firebase CLI** (ya instalado en tu equipo):
   ```bash
   firebase login
   ```

2. **Genera `lib/firebase_options.dart` y `google-services.json` con FlutterFire CLI**:
   ```bash
   dart pub global activate flutterfire_cli
   cd apps/mobile
   flutterfire configure
   ```
   *(También puedes descargar `google-services.json` desde Firebase Console y colocarlo en `apps/mobile/android/app/google-services.json`.)*

3. **Habilita Firestore y Storage en Firebase Console**:
   - **Firestore Database** → Crear base de datos.
   - **Storage** → Habilitar bucket por defecto.
   - Aplica las reglas incluidas en `firestore.rules` y `storage.rules`.

Al iniciar la app, el indicador superior cambiará automáticamente a **Firebase** y sincronizará `staff`, `vehicles`, `parts`, `devices` y `enrollment_codes` en tiempo real (sembrando datos iniciales automáticamente si la base está vacía).
