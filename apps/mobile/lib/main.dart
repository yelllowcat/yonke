import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/widgets.dart';
import 'data/firebase_repository.dart';
import 'data/yonke_store.dart';
import 'firebase_options.dart';
import 'navigation/app_navigator.dart';
import 'screens/login_screen.dart';
import 'theme/stitch_theme.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  FirebaseYonkeRepository? firebaseRepo;
  if (DefaultFirebaseOptions.isConfigured) {
    try {
      await Firebase.initializeApp(
        options: DefaultFirebaseOptions.currentPlatform,
      );
      firebaseRepo = FirebaseYonkeRepository();
    } catch (_) {
      firebaseRepo = null;
    }
  } else {
    // Also support native google-services.json / GoogleService-Info.plist if dropped in.
    try {
      await Firebase.initializeApp();
      firebaseRepo = FirebaseYonkeRepository();
    } catch (_) {
      firebaseRepo = null;
    }
  }

  runApp(YonkeFacilApp(store: YonkeStore(firebaseRepository: firebaseRepo)));
}

/// Root app following the exact pure `WidgetsApp` + `PageRouteBuilder`
/// navigation architecture of https://github.com/aojedacs/eventhub-flutter.
class YonkeFacilApp extends StatelessWidget {
  YonkeFacilApp({super.key, YonkeStore? store}) : store = store ?? YonkeStore();

  final YonkeStore store;

  @override
  Widget build(BuildContext context) {
    return WidgetsApp(
      title: 'Yonke Fácil',
      color: StitchColors.primary,
      debugShowCheckedModeBanner: false,
      textStyle: StitchText.bodyLg,
      pageRouteBuilder: <T>(
        RouteSettings settings,
        WidgetBuilder builder,
      ) {
        return PageRouteBuilder<T>(
          settings: settings,
          transitionDuration: const Duration(milliseconds: 200),
          reverseTransitionDuration: const Duration(milliseconds: 120),
          pageBuilder: (context, animation, secondaryAnimation) {
            return builder(context);
          },
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(opacity: animation, child: child);
          },
        );
      },
      onGenerateRoute: (settings) => AppNavigator.buildRoute(
        LoginScreen(store: store),
        settings: settings,
      ),
      home: LoginScreen(store: store),
    );
  }
}
