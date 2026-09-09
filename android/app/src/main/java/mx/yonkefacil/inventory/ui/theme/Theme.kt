package mx.yonkefacil.inventory.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Navy = Color(0xFF17324D)
val NavyDark = Color(0xFF0F2538)
val SafetyYellow = Color(0xFFF4C542)
val Sky = Color(0xFF2C77B8)
val Paper = Color(0xFFF7F9FA)
val PaperWarm = Color(0xFFFFFCF6)
val Steel = Color(0xFF5F6B75)
val Success = Color(0xFF287A4B)
val SuccessSoft = Color(0xFFE3F3E9)
val SkySoft = Color(0xFFE7F2FB)
val YellowSoft = Color(0xFFFFF3BC)
val ErrorRed = Color(0xFFA43B35)

private val colors = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = SafetyYellow,
    onPrimaryContainer = NavyDark,
    secondary = Sky,
    onSecondary = Color.White,
    background = Paper,
    onBackground = NavyDark,
    surface = Color.White,
    onSurface = NavyDark,
    surfaceVariant = Color(0xFFE8EDF0),
    onSurfaceVariant = Steel,
    outline = Color(0xFFB9C5CC),
    outlineVariant = Color(0xFFD5DEE3),
    error = ErrorRed,
)

private val type = Typography(
    displaySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 42.sp, lineHeight = 45.sp, letterSpacing = (-.5).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 33.sp, lineHeight = 38.sp, letterSpacing = (-.3).sp, lineBreak = LineBreak.Paragraph),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 27.sp, lineHeight = 33.sp, letterSpacing = (-.2).sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 23.sp, lineHeight = 29.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 25.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 27.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 22.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = .2.sp),
)

@Composable
fun YonkeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colors,
        typography = type,
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp),
        ),
        content = content,
    )
}
