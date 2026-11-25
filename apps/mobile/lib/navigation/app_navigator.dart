import 'package:flutter/widgets.dart';

/// Pure Flutter Navigator helpers following the exact pattern from
/// https://github.com/aojedacs/eventhub-flutter
/// (`WidgetsApp` + `PageRouteBuilder` + `Navigator.push` / `pop` / `popUntil`).
abstract final class AppNavigator {
  static PageRouteBuilder<T> buildRoute<T>(
    Widget screen, {
    RouteSettings? settings,
  }) {
    return PageRouteBuilder<T>(
      settings: settings,
      transitionDuration: const Duration(milliseconds: 200),
      reverseTransitionDuration: const Duration(milliseconds: 120),
      pageBuilder: (context, animation, secondaryAnimation) => screen,
      transitionsBuilder: (context, animation, secondaryAnimation, child) {
        return FadeTransition(opacity: animation, child: child);
      },
    );
  }

  static Future<T?> push<T>(BuildContext context, Widget screen) {
    return Navigator.push<T>(context, buildRoute<T>(screen));
  }

  static Future<T?> pushReplacement<T, TO>(BuildContext context, Widget screen) {
    return Navigator.pushReplacement<T, TO>(context, buildRoute<T>(screen));
  }

  static void pop<T>(BuildContext context, [T? result]) {
    if (Navigator.canPop(context)) {
      Navigator.pop<T>(context, result);
    }
  }

  /// Returns to the first route (LoginScreen) on sign-out, matching
  /// `Navigator.popUntil(context, (route) => route.isFirst)` in eventhub-flutter.
  static void popToLogin(BuildContext context) {
    Navigator.popUntil(context, (route) => route.isFirst);
  }
}
