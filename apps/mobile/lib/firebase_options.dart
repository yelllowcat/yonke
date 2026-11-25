import 'package:firebase_core/firebase_core.dart' show FirebaseOptions;
import 'package:flutter/foundation.dart'
    show defaultTargetPlatform, kIsWeb, TargetPlatform;

/// Replace this file by running:
///   dart pub global activate flutterfire_cli
///   flutterfire configure
/// inside `apps/mobile/`.
class DefaultFirebaseOptions {
  static const String _placeholderProjectId = 'YOUR_FIREBASE_PROJECT_ID';

  static bool get isConfigured =>
      android.projectId != _placeholderProjectId &&
      android.apiKey != 'YOUR_ANDROID_API_KEY';

  static FirebaseOptions get currentPlatform {
    if (kIsWeb) {
      return web;
    }
    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        return android;
      case TargetPlatform.iOS:
        return ios;
      case TargetPlatform.macOS:
        return ios;
      default:
        return android;
    }
  }

  static const FirebaseOptions android = FirebaseOptions(
    apiKey: 'YOUR_ANDROID_API_KEY',
    appId: '1:000000000000:android:0000000000000000000000',
    messagingSenderId: '000000000000',
    projectId: _placeholderProjectId,
    storageBucket: '$_placeholderProjectId.firebasestorage.app',
  );

  static const FirebaseOptions ios = FirebaseOptions(
    apiKey: 'YOUR_IOS_API_KEY',
    appId: '1:000000000000:ios:0000000000000000000000',
    messagingSenderId: '000000000000',
    projectId: _placeholderProjectId,
    storageBucket: '$_placeholderProjectId.firebasestorage.app',
    iosBundleId: 'mx.yonkefacil.yonkeMobile',
  );

  static const FirebaseOptions web = FirebaseOptions(
    apiKey: 'YOUR_WEB_API_KEY',
    appId: '1:000000000000:web:0000000000000000000000',
    messagingSenderId: '000000000000',
    projectId: _placeholderProjectId,
    storageBucket: '$_placeholderProjectId.firebasestorage.app',
  );
}
