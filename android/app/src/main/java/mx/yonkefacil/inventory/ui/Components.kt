package mx.yonkefacil.inventory.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.yonkefacil.inventory.data.AppLanguage
import mx.yonkefacil.inventory.data.InventoryStatus
import mx.yonkefacil.inventory.data.Part
import mx.yonkefacil.inventory.data.Vehicle
import mx.yonkefacil.inventory.ui.theme.Navy
import mx.yonkefacil.inventory.ui.theme.SafetyYellow
import mx.yonkefacil.inventory.ui.theme.Sky
import mx.yonkefacil.inventory.ui.theme.SkySoft
import mx.yonkefacil.inventory.ui.theme.Success
import mx.yonkefacil.inventory.ui.theme.SuccessSoft

@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Surface(color = Navy, contentColor = Color.White, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().height(76.dp).padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.size(60.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back / Atrás", modifier = Modifier.size(30.dp))
                }
            } else {
                Surface(color = SafetyYellow, shape = RoundedCornerShape(10.dp), modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = Navy, modifier = Modifier.size(29.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f).semantics { heading() },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            trailing?.invoke()
        }
    }
}

@Composable
fun LanguageButton(language: AppLanguage, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
        border = BorderStroke(2.dp, Color.White),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
    ) {
        Icon(Icons.Default.Language, contentDescription = "Change language / Cambiar idioma", modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(6.dp))
        Text(if (language == AppLanguage.Spanish) "EN" else "ES", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(64.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Navy, contentColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        icon?.let {
            Icon(it, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(9.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(60.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        icon?.let {
            Icon(it, contentDescription = null, modifier = Modifier.size(23.dp))
            Spacer(Modifier.width(9.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SectionCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), content = {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Navy)
            Spacer(Modifier.height(13.dp))
            content()
        })
    }
}

@Composable
fun ActionCard(title: String, help: String, icon: ImageVector, accent: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(136.dp).clickable(role = Role.Button, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(72.dp).background(accent, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, contentDescription = null, tint = Navy, modifier = Modifier.size(37.dp)) }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(help, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Sky, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun InventoryTag(reference: String) {
    Surface(color = SafetyYellow, contentColor = Navy, shape = RoundedCornerShape(3.dp), modifier = Modifier.rotate(-1f)) {
        Text(reference, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontWeight = FontWeight.ExtraBold, letterSpacing = .5.sp)
    }
}

@Composable
fun StatusPill(text: String, positive: Boolean) {
    Surface(
        color = if (positive) SuccessSoft else Color(0xFFFFE6E3),
        contentColor = if (positive) Success else MaterialTheme.colorScheme.error,
        shape = RoundedCornerShape(50),
    ) {
        Row(Modifier.padding(horizontal = 9.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).background(if (positive) Success else MaterialTheme.colorScheme.error, RoundedCornerShape(50)))
            Spacer(Modifier.width(5.dp))
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun PartRow(
    part: Part,
    vehicle: Vehicle?,
    language: AppLanguage,
    copy: Copy,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                InventoryTag(part.reference)
                StatusPill(if (part.status == InventoryStatus.Available) copy.available else copy.sold, part.status == InventoryStatus.Available)
            }
            Spacer(Modifier.height(12.dp))
            Text(part.name(language), style = MaterialTheme.typography.titleLarge)
            Text(
                vehicle?.let { "${it.year} ${it.make} ${it.model}" } ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = Sky,
                fontWeight = FontWeight.Bold,
            )
            if (part.locationNote.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(part.locationNote, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(10.dp))
            Text(part.priceMxn?.let { "$${"%,d".format(it)} MXN" } ?: copy.askPrice, style = MaterialTheme.typography.titleMedium, color = Navy)
        }
    }
}

@Composable
fun SavedBanner(copy: Copy) {
    Surface(color = SuccessSoft, contentColor = Success) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success, modifier = Modifier.size(25.dp))
            Spacer(Modifier.width(9.dp))
            Text(copy.saved, color = Success, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun HelperBanner(text: String, positive: Boolean = false) {
    Surface(color = if (positive) SuccessSoft else SkySoft, contentColor = if (positive) Success else Navy, shape = RoundedCornerShape(10.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(if (positive) Success else Sky, RoundedCornerShape(50)))
            Spacer(Modifier.width(9.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ErrorBanner(text: String) {
    Surface(color = Color(0xFFFFE6E3), contentColor = MaterialTheme.colorScheme.error, shape = RoundedCornerShape(10.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.error, RoundedCornerShape(50)))
            Spacer(Modifier.width(9.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

fun actionIcon(index: Int): ImageVector = when (index) {
    0 -> Icons.Default.DirectionsCar
    1 -> Icons.Default.Build
    2 -> Icons.Default.Search
    3 -> Icons.Default.PointOfSale
    else -> Icons.Default.Inventory2
}
