import 'package:flutter/widgets.dart';

/// Yonke Fácil — Sistema Utilitario de Patio (Stitch Design Tokens)
abstract final class StitchColors {
  static const primary = Color(0xFFA33700);
  static const onPrimary = Color(0xFFFFFFFF);
  static const primaryContainer = Color(0xFFFFDBCF);
  static const onPrimaryContainer = Color(0xFF380D00);

  static const secondary = Color(0xFF1D4ED8);
  static const onSecondary = Color(0xFFFFFFFF);
  static const secondaryContainer = Color(0xFFDCE1FF);
  static const onSecondaryContainer = Color(0xFF001551);

  static const tertiary = Color(0xFF005BAD);
  static const onTertiary = Color(0xFFFFFFFF);
  static const tertiaryContainer = Color(0xFFD5E3FF);
  static const onTertiaryContainer = Color(0xFF001B3C);

  static const success = Color(0xFF166534);
  static const onSuccess = Color(0xFFFFFFFF);
  static const successContainer = Color(0xFFDCFCE7);
  static const onSuccessContainer = Color(0xFF14532D);

  static const warning = Color(0xFFB45309);
  static const onWarning = Color(0xFFFFFFFF);
  static const warningContainer = Color(0xFFFEF3C7);
  static const onWarningContainer = Color(0xFF78350F);

  static const error = Color(0xFFBA1A1A);
  static const onError = Color(0xFFFFFFFF);
  static const errorContainer = Color(0xFFFFDAD6);
  static const onErrorContainer = Color(0xFF93000A);

  static const background = Color(0xFFFAF9F6);
  static const surface = Color(0xFFFAF9F6);
  static const surfaceContainerLowest = Color(0xFFFFFFFF);
  static const surfaceContainerLow = Color(0xFFF4F3F0);
  static const surfaceContainer = Color(0xFFEFEEEB);
  static const surfaceContainerHigh = Color(0xFFE9E8E5);
  static const surfaceContainerHighest = Color(0xFFE3E2DF);

  static const onSurface = Color(0xFF1A1C1A);
  static const onSurfaceVariant = Color(0xFF464745);
  static const outline = Color(0xFF5E5F5C);
  static const outlineVariant = Color(0xFFC7C6C4);

  static const inverseSurface = Color(0xFF1A1C1A);
  static const inverseOnSurface = Color(0xFFF2F1EE);
}

abstract final class StitchRadius {
  static const double sm = 4;
  static const double card = 6;
  static const double md = 8;
  static const double lg = 12;
  static const double full = 9999;
}

abstract final class StitchSpacing {
  static const double xs = 4;
  static const double sm = 8;
  static const double md = 12;
  static const double lg = 16;
  static const double xl = 24;
  static const double section = 32;
  static const double pageMargin = 16;
}

abstract final class StitchText {
  static const displayMobile = TextStyle(
    fontFamily: 'Archivo',
    fontSize: 30,
    fontWeight: FontWeight.w800,
    height: 34 / 30,
    letterSpacing: -0.25,
    color: StitchColors.onSurface,
  );

  static const headlineLgMobile = TextStyle(
    fontFamily: 'Archivo',
    fontSize: 24,
    fontWeight: FontWeight.w700,
    height: 28 / 24,
    letterSpacing: -0.2,
    color: StitchColors.onSurface,
  );

  static const headlineMd = TextStyle(
    fontFamily: 'Archivo',
    fontSize: 20,
    fontWeight: FontWeight.w700,
    height: 24 / 20,
    color: StitchColors.onSurface,
  );

  static const titleLg = TextStyle(
    fontFamily: 'Commissioner',
    fontSize: 18,
    fontWeight: FontWeight.w700,
    height: 24 / 18,
    color: StitchColors.onSurface,
  );

  static const titleMd = TextStyle(
    fontFamily: 'Commissioner',
    fontSize: 16,
    fontWeight: FontWeight.w600,
    height: 22 / 16,
    color: StitchColors.onSurface,
  );

  static const bodyLg = TextStyle(
    fontFamily: 'Commissioner',
    fontSize: 16,
    fontWeight: FontWeight.w500,
    height: 24 / 16,
    color: StitchColors.onSurface,
  );

  static const bodyMd = TextStyle(
    fontFamily: 'Commissioner',
    fontSize: 14,
    fontWeight: FontWeight.w500,
    height: 20 / 14,
    color: StitchColors.onSurfaceVariant,
  );

  static const labelLg = TextStyle(
    fontFamily: 'Commissioner',
    fontSize: 15,
    fontWeight: FontWeight.w700,
    height: 20 / 15,
    letterSpacing: 0.25,
    color: StitchColors.onSurface,
  );

  static const labelSm = TextStyle(
    fontFamily: 'Commissioner',
    fontSize: 12,
    fontWeight: FontWeight.w700,
    height: 16 / 12,
    letterSpacing: 0.4,
    color: StitchColors.onSurfaceVariant,
  );

  static const monoDisplay = TextStyle(
    fontFamily: 'SourceCodePro',
    fontSize: 30,
    fontWeight: FontWeight.w700,
    height: 34 / 30,
    letterSpacing: -0.25,
    color: StitchColors.onSurface,
    fontFeatures: [FontFeature.tabularFigures()],
  );

  static const monoLg = TextStyle(
    fontFamily: 'SourceCodePro',
    fontSize: 22,
    fontWeight: FontWeight.w700,
    height: 26 / 22,
    color: StitchColors.onSurface,
    fontFeatures: [FontFeature.tabularFigures()],
  );

  static const monoMd = TextStyle(
    fontFamily: 'SourceCodePro',
    fontSize: 14,
    fontWeight: FontWeight.w600,
    height: 18 / 14,
    color: StitchColors.onSurfaceVariant,
    fontFeatures: [FontFeature.tabularFigures()],
  );
}
