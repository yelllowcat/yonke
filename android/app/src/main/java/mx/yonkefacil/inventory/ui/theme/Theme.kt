package mx.yonkefacil.inventory.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.yonkefacil.inventory.R

// Yonke Fácil — Sistema Utilitario de Patio (Stitch Design Tokens)
val PrimaryOrange = Color(0xFFA33700)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFFFDBCF)
val OnPrimaryContainer = Color(0xFF380D00)

val SecondaryCobalt = Color(0xFF1D4ED8)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFDCE1FF)
val OnSecondaryContainer = Color(0xFF001551)

val TertiaryBlue = Color(0xFF005BAD)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFD5E3FF)
val OnTertiaryContainer = Color(0xFF001B3C)

val SuccessGreen = Color(0xFF166534)
val OnSuccess = Color(0xFFFFFFFF)
val SuccessContainer = Color(0xFFDCFCE7)
val OnSuccessContainer = Color(0xFF14532D)

val WarningAmber = Color(0xFFB45309)
val OnWarning = Color(0xFFFFFFFF)
val WarningContainer = Color(0xFFFEF3C7)
val OnWarningContainer = Color(0xFF78350F)

val ErrorRed = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

val SurfaceWarm = Color(0xFFFAF9F6)
val SurfaceLowest = Color(0xFFFFFFFF)
val SurfaceLow = Color(0xFFF4F3F0)
val SurfaceContainer = Color(0xFFEFEEEB)
val SurfaceHigh = Color(0xFFE9E8E5)
val SurfaceHighest = Color(0xFFE3E2DF)

val OnSurfaceInk = Color(0xFF1A1C1A)
val OnSurfaceMuted = Color(0xFF464745)
val OutlineBorder = Color(0xFF5E5F5C)
val OutlineSubtle = Color(0xFFC7C6C4)

val InverseSurface = Color(0xFF1A1C1A)
val InverseOnSurface = Color(0xFFF2F1EE)

val ArchivoFontFamily = FontFamily(
    Font(R.font.archivo_600, FontWeight.SemiBold),
    Font(R.font.archivo_700, FontWeight.Bold),
    Font(R.font.archivo_800, FontWeight.ExtraBold),
)

val CommissionerFontFamily = FontFamily(
    Font(R.font.commissioner_500, FontWeight.Normal),
    Font(R.font.commissioner_500, FontWeight.Medium),
    Font(R.font.commissioner_600, FontWeight.SemiBold),
    Font(R.font.commissioner_700, FontWeight.Bold),
)

val SourceCodeProFontFamily = FontFamily(
    Font(R.font.source_code_pro_600, FontWeight.SemiBold),
    Font(R.font.source_code_pro_700, FontWeight.Bold),
)

val MonoDisplayStyle = TextStyle(
    fontFamily = SourceCodeProFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp,
    lineHeight = 34.sp,
    letterSpacing = (-0.25).sp,
    fontFeatureSettings = "tnum",
)

val MonoLargeStyle = TextStyle(
    fontFamily = SourceCodeProFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.sp,
    fontFeatureSettings = "tnum",
)

val MonoMediumStyle = TextStyle(
    fontFamily = SourceCodeProFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.sp,
    fontFeatureSettings = "tnum",
)

private val StitchLightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryCobalt,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryBlue,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = SurfaceWarm,
    onBackground = OnSurfaceInk,
    surface = SurfaceWarm,
    onSurface = OnSurfaceInk,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = OnSurfaceMuted,
    surfaceContainerLowest = SurfaceLowest,
    surfaceContainerLow = SurfaceLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceHigh,
    surfaceContainerHighest = SurfaceHighest,
    outline = OutlineBorder,
    outlineVariant = OutlineSubtle,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    error = ErrorRed,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
)

private val StitchTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ArchivoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = ArchivoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.25).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = ArchivoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp,
        lineBreak = LineBreak.Paragraph,
    ),
    headlineMedium = TextStyle(
        fontFamily = ArchivoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = CommissionerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
)

@Composable
fun YonkeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StitchLightColorScheme,
        typography = StitchTypography,
        shapes = MaterialTheme.shapes.copy(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(6.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(12.dp),
        ),
        content = content,
    )
}
