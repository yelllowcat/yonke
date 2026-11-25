package mx.yonkefacil.inventory.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.yonkefacil.inventory.data.AppLanguage
import mx.yonkefacil.inventory.data.InventoryStatus
import mx.yonkefacil.inventory.data.Part
import mx.yonkefacil.inventory.data.Vehicle
import mx.yonkefacil.inventory.ui.theme.ErrorContainer
import mx.yonkefacil.inventory.ui.theme.ErrorRed
import mx.yonkefacil.inventory.ui.theme.MonoLargeStyle
import mx.yonkefacil.inventory.ui.theme.MonoMediumStyle
import mx.yonkefacil.inventory.ui.theme.OnErrorContainer
import mx.yonkefacil.inventory.ui.theme.OnPrimary
import mx.yonkefacil.inventory.ui.theme.OnPrimaryContainer
import mx.yonkefacil.inventory.ui.theme.OnSecondary
import mx.yonkefacil.inventory.ui.theme.OnSecondaryContainer
import mx.yonkefacil.inventory.ui.theme.OnSuccessContainer
import mx.yonkefacil.inventory.ui.theme.OnSurfaceInk
import mx.yonkefacil.inventory.ui.theme.OnSurfaceMuted
import mx.yonkefacil.inventory.ui.theme.OnWarningContainer
import mx.yonkefacil.inventory.ui.theme.OutlineBorder
import mx.yonkefacil.inventory.ui.theme.OutlineSubtle
import mx.yonkefacil.inventory.ui.theme.PrimaryContainer
import mx.yonkefacil.inventory.ui.theme.PrimaryOrange
import mx.yonkefacil.inventory.ui.theme.SecondaryCobalt
import mx.yonkefacil.inventory.ui.theme.SecondaryContainer
import mx.yonkefacil.inventory.ui.theme.SuccessContainer
import mx.yonkefacil.inventory.ui.theme.SuccessGreen
import mx.yonkefacil.inventory.ui.theme.SurfaceContainer
import mx.yonkefacil.inventory.ui.theme.SurfaceHigh
import mx.yonkefacil.inventory.ui.theme.SurfaceLowest
import mx.yonkefacil.inventory.ui.theme.SurfaceWarm
import mx.yonkefacil.inventory.ui.theme.WarningAmber
import mx.yonkefacil.inventory.ui.theme.WarningContainer

enum class YardTab { Home, Inventory, Sales, Team }

@Composable
fun YardTopBar(
    title: String,
    subtitle: String,
    syncText: String,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
) {
    Surface(color = SurfaceWarm, contentColor = OnSurfaceInk) {
        Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryOrange)
                            .border(2.dp, PrimaryOrange, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CarRepair,
                            contentDescription = null,
                            tint = OnPrimary,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnSurfaceInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.semantics { heading() },
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    StatusBadge(
                        text = syncText,
                        containerColor = SuccessContainer,
                        contentColor = OnSuccessContainer,
                        borderColor = SuccessGreen,
                        icon = Icons.Default.CloudDone,
                    )
                    LanguageChip(language = language, onClick = onToggleLanguage)
                }
            }
            HorizontalDivider(thickness = 2.dp, color = OutlineSubtle)
        }
    }
}

@Composable
fun ContextualBackHeader(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    Surface(color = SurfaceWarm, contentColor = OnSurfaceInk) {
        Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    IconSquareButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar / Back",
                        onClick = onBack,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnSurfaceInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.semantics { heading() },
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                if (trailing != null) {
                    Spacer(Modifier.width(8.dp))
                    trailing()
                }
            }
            HorizontalDivider(thickness = 2.dp, color = OutlineSubtle)
        }
    }
}

@Composable
fun LanguageChip(language: AppLanguage, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .defaultMinSize(minWidth = 40.dp, minHeight = 36.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(role = Role.Button, onClick = onClick),
        color = SurfaceLowest,
        contentColor = OnSurfaceInk,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(2.dp, OutlineSubtle),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (language == AppLanguage.Spanish) "EN" else "ES",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceInk,
            )
        }
    }
}

@Composable
fun BottomYardNavBar(
    currentTab: YardTab,
    copy: Copy,
    onSelectTab: (YardTab) -> Unit,
    onCameraClick: () -> Unit,
) {
    Surface(
        color = SurfaceLowest,
        contentColor = OnSurfaceInk,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
            HorizontalDivider(thickness = 2.dp, color = OutlineBorder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NavTabItem(
                    label = copy.navHome,
                    icon = Icons.Default.Home,
                    selected = currentTab == YardTab.Home,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectTab(YardTab.Home) },
                )
                NavTabItem(
                    label = copy.navInventory,
                    icon = Icons.Default.Inventory2,
                    selected = currentTab == YardTab.Inventory,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectTab(YardTab.Inventory) },
                )
                // Central Elevated Camera Action (Stitch 5-column bottom bar)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = (-12).dp)
                        .clickable(role = Role.Button, onClick = onCameraClick),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PrimaryOrange)
                            .border(2.dp, SurfaceWarm, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = copy.navCamera,
                            tint = OnPrimary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = copy.navCamera,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryOrange,
                        maxLines = 1,
                    )
                }
                NavTabItem(
                    label = copy.navSales,
                    icon = Icons.Default.ReceiptLong,
                    selected = currentTab == YardTab.Sales,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectTab(YardTab.Sales) },
                )
                NavTabItem(
                    label = copy.navTeam,
                    icon = Icons.Default.Group,
                    selected = currentTab == YardTab.Team,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectTab(YardTab.Team) },
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val tint = if (selected) PrimaryOrange else OnSurfaceMuted
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { this.selected = selected }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    containerColor: Color = PrimaryOrange,
) {
    val borderColor = if (enabled) containerColor else OutlineSubtle
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 54.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = OnPrimary,
            disabledContainerColor = SurfaceHigh,
            disabledContentColor = OutlineBorder,
        ),
        border = BorderStroke(2.dp, borderColor),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    subtleBorder: Boolean = false,
) {
    val borderColor = when {
        !enabled -> OutlineSubtle
        subtleBorder -> OutlineSubtle
        else -> SecondaryCobalt
    }
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SurfaceLowest,
            contentColor = SecondaryCobalt,
            disabledContainerColor = SurfaceHigh,
            disabledContentColor = OutlineBorder,
        ),
        border = BorderStroke(2.dp, borderColor),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun IconSquareButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick),
        color = SurfaceLowest,
        contentColor = OnSurfaceInk,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, OutlineBorder),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun YardFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    minHeightDp: Int = 44,
) {
    val bg = if (selected) SecondaryCobalt else SurfaceLowest
    val fg = if (selected) OnSecondary else OnSurfaceInk
    val border = if (selected) SecondaryCobalt else OutlineSubtle

    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = minHeightDp.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { this.selected = selected },
        color = bg,
        contentColor = fg,
        shape = CircleShape,
        border = BorderStroke(2.dp, border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color? = null,
    icon: ImageVector? = null,
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(4.dp),
        border = borderColor?.let { BorderStroke(1.dp, it) },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(14.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelSmall, color = contentColor, maxLines = 1)
        }
    }
}

@Composable
fun MetricKpiCard(
    label: String,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = SurfaceLowest,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(2.dp, OutlineSubtle),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Text(text = value, style = MonoLargeStyle, color = OnSurfaceInk)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = subtitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun YardSectionCard(
    title: String? = null,
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceLowest,
    borderColor: Color = OutlineSubtle,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(2.dp, borderColor),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurfaceInk,
                )
            }
            content()
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    help: String,
    icon: ImageVector,
    highlighted: Boolean = false,
    onClick: () -> Unit,
) {
    val bg = if (highlighted) PrimaryContainer else SurfaceLowest
    val border = if (highlighted) PrimaryOrange else OutlineSubtle
    val titleColor = if (highlighted) OnPrimaryContainer else OnSurfaceInk
    val helpColor = if (highlighted) OnPrimaryContainer else OnSurfaceMuted
    val iconColor = if (highlighted) PrimaryOrange else SecondaryCobalt

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 84.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(role = Role.Button, onClick = onClick),
        color = bg,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(2.dp, border),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (highlighted) PrimaryOrange else SurfaceContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlighted) OnPrimary else iconColor,
                    modifier = Modifier.size(26.dp),
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = help,
                    style = MaterialTheme.typography.bodyMedium,
                    color = helpColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (highlighted) PrimaryOrange else OutlineBorder,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
fun PartActionCard(
    part: Part,
    vehicle: Vehicle?,
    language: AppLanguage,
    copy: Copy,
    onOpenDetail: () -> Unit,
    onQuickAction: (() -> Unit)? = null,
    quickActionLabel: String? = null,
    quickActionIcon: ImageVector = Icons.Default.PointOfSale,
    isRestoreAction: Boolean = false,
) {
    val isAvailable = part.status == InventoryStatus.Available
    val statusBg = if (isAvailable) SuccessContainer else ErrorContainer
    val statusFg = if (isAvailable) OnSuccessContainer else OnErrorContainer
    val statusText = if (isAvailable) copy.available else copy.sold

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(role = Role.Button, onClick = onOpenDetail),
        color = SurfaceLowest,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(2.dp, if (isAvailable) OutlineBorder else OutlineSubtle),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                // Industrial 88dp thumbnail slot
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainer)
                        .border(1.dp, OutlineSubtle, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (part.hasPhoto) Icons.Default.Build else Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = OutlineBorder,
                            modifier = Modifier.size(30.dp),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = part.reference,
                            style = MonoMediumStyle,
                            color = OnSurfaceMuted,
                            maxLines = 1,
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StatusBadge(text = statusText, containerColor = statusBg, contentColor = statusFg)
                        Text(
                            text = part.locationNote.ifBlank { part.reference },
                            style = MonoMediumStyle,
                            color = OnSurfaceMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                    Text(
                        text = part.name(language),
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurfaceInk,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val vehicleLine = buildString {
                        if (vehicle != null) {
                            append("${vehicle.make} ${vehicle.model} · ${vehicle.year}")
                        }
                        if (isNotEmpty()) append(" · ")
                        append(copy.condition(part.condition))
                    }
                    Text(
                        text = vehicleLine,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            HorizontalDivider(thickness = 1.dp, color = OutlineSubtle)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = copy.cashPrice,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceMuted,
                    )
                    Text(
                        text = formatMxn(part.priceMxn, copy.askPrice),
                        style = MonoLargeStyle,
                        color = OnSurfaceInk,
                    )
                }
                if (onQuickAction != null && quickActionLabel != null) {
                    if (isRestoreAction) {
                        OutlinedButton(
                            onClick = onQuickAction,
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceLowest,
                                contentColor = SecondaryCobalt,
                            ),
                            border = BorderStroke(2.dp, SecondaryCobalt),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(quickActionLabel, style = MaterialTheme.typography.labelLarge)
                        }
                    } else {
                        Button(
                            onClick = onQuickAction,
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryOrange,
                                contentColor = OnPrimary,
                            ),
                            border = BorderStroke(2.dp, PrimaryOrange),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Icon(quickActionIcon, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(quickActionLabel, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    hint: String? = null,
    leadingIcon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (label.isNotBlank()) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = OnSurfaceInk)
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 52.dp),
            placeholder = placeholder?.let { { Text(it, style = MaterialTheme.typography.bodyLarge, color = OutlineBorder) } },
            leadingIcon = leadingIcon?.let {
                { Icon(it, contentDescription = null, tint = OutlineBorder, modifier = Modifier.size(22.dp)) }
            },
            trailingIcon = trailing,
            singleLine = singleLine,
            minLines = minLines,
            isError = isError,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = OnSurfaceInk),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceLowest,
                unfocusedContainerColor = SurfaceLowest,
                errorContainerColor = SurfaceLowest,
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = OutlineBorder,
                errorBorderColor = ErrorRed,
                cursorColor = PrimaryOrange,
            ),
        )
        if (!hint.isNullOrBlank()) {
            Text(text = hint, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
        }
    }
}

@Composable
fun HelperBanner(text: String, positive: Boolean = false) {
    val bg = if (positive) SuccessContainer else SecondaryContainer
    val fg = if (positive) OnSuccessContainer else OnSecondaryContainer
    val border = if (positive) SuccessGreen else SecondaryCobalt
    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, border),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(8.dp).background(border, CircleShape))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = fg)
        }
    }
}

@Composable
fun ErrorBanner(text: String) {
    Surface(
        color = ErrorContainer,
        contentColor = OnErrorContainer,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(2.dp, ErrorRed),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(8.dp).background(ErrorRed, CircleShape))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = OnErrorContainer)
        }
    }
}

@Composable
fun EmptyMessage(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceLowest,
        border = BorderStroke(2.dp, OutlineSubtle),
        shape = RoundedCornerShape(6.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Default.Inventory2, contentDescription = null, tint = OutlineBorder, modifier = Modifier.size(40.dp))
            Text(message, style = MaterialTheme.typography.titleMedium, color = OnSurfaceMuted)
        }
    }
}

fun formatMxn(priceMxn: Int?, fallback: String): String =
    priceMxn?.let { "$${"%,d".format(it)}" } ?: fallback

fun formatSixDigitCode(code: String): String {
    val clean = code.filter(Char::isDigit)
    return if (clean.length == 6) "${clean.substring(0, 3)} ${clean.substring(3, 6)}" else code
}

fun actionIcon(index: Int): ImageVector = when (index) {
    0 -> Icons.Default.DirectionsCar
    1 -> Icons.Default.Build
    2 -> Icons.Default.Search
    3 -> Icons.Default.PointOfSale
    else -> Icons.Default.Inventory2
}
