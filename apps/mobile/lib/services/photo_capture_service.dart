import 'package:image_picker/image_picker.dart';

/// Abstraction over `image_picker` so screens can open the native camera
/// (with automatic gallery fallback on emulators/desktop) and remain testable.
class PhotoCaptureService {
  PhotoCaptureService({ImagePicker? picker}) : _picker = picker ?? ImagePicker();

  final ImagePicker _picker;

  Future<String?> captureOrPick() async {
    try {
      final shot = await _picker.pickImage(
        source: ImageSource.camera,
        imageQuality: 84,
        maxWidth: 1600,
      );
      if (shot != null) return shot.path;
    } catch (_) {
      // Camera unavailable on this device/emulator; fall back to gallery.
    }
    try {
      final picked = await _picker.pickImage(
        source: ImageSource.gallery,
        imageQuality: 84,
        maxWidth: 1600,
      );
      return picked?.path;
    } catch (_) {
      return null;
    }
  }
}
