import 'package:flutter/widgets.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../screens/camera_intake_screen.dart';
import '../screens/home_screen.dart';
import '../screens/inventory_screen.dart';
import '../screens/sales_screen.dart';
import '../screens/team_owner_screen.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';
import 'custom_button.dart';

enum YardTab { home, inventory, sales, team }

/// Shared mobile shell with Stitch top bar + 5-column bottom bar (4 tabs + central camera)
/// using `AppNavigator.pushReplacement` / `AppNavigator.push` (`PageRouteBuilder`).
class YardScaffold extends StatelessWidget {
  const YardScaffold({
    super.key,
    required this.store,
    required this.title,
    required this.currentTab,
    required this.body,
  });

  final YonkeStore store;
  final String title;
  final YardTab currentTab;
  final Widget body;

  void _navigateToTab(BuildContext context, YardTab target) {
    if (target == currentTab) return;
    final Widget next = switch (target) {
      YardTab.home => HomeScreen(store: store),
      YardTab.inventory => InventoryScreen(store: store),
      YardTab.sales => SalesScreen(store: store),
      YardTab.team => TeamOwnerScreen(store: store),
    };
    AppNavigator.pushReplacement(context, next);
  }

  @override
  Widget build(BuildContext context) {
    final user = store.currentUser;
    final shiftText = user == null
        ? 'Turno activo'
        : user.role == StaffRole.owner
            ? 'Turno: ${user.name} (Dueño)'
            : 'Turno: ${user.name}';

    return ColoredBox(
      color: StitchColors.surface,
      child: SafeArea(
        child: Column(
          children: [
            YardTopBar(title: title, subtitle: shiftText),
            Expanded(child: body),
            BottomYardNavBar(
              currentTab: currentTab,
              onSelectTab: (tab) => _navigateToTab(context, tab),
              onCameraTap: () => AppNavigator.push(
                context,
                CameraIntakeScreen(store: store),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class YardTopBar extends StatelessWidget {
  const YardTopBar({
    super.key,
    required this.title,
    required this.subtitle,
  });

  final String title;
  final String subtitle;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: const BoxDecoration(
        color: StitchColors.surface,
        border: Border(
          bottom: BorderSide(color: StitchColors.outlineVariant, width: 2),
        ),
      ),
      child: Row(
        children: [
          Container(
            width: 36,
            height: 36,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: StitchColors.primary,
              borderRadius: BorderRadius.circular(StitchRadius.md),
              border: Border.all(color: StitchColors.primary, width: 2),
            ),
            child: const Icon(
              StitchIcons.carRepair,
              size: 20,
              color: StitchColors.onPrimary,
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: StitchText.headlineMd,
                ),
                Text(
                  subtitle,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: StitchText.labelSm,
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          const StatusBadge(
            text: 'Al día',
            backgroundColor: StitchColors.successContainer,
            textColor: StitchColors.onSuccessContainer,
            borderColor: StitchColors.success,
            icon: StitchIcons.cloudDone,
          ),
        ],
      ),
    );
  }
}

class ContextualBackHeader extends StatelessWidget {
  const ContextualBackHeader({
    super.key,
    required this.title,
    this.subtitle,
    required this.onBack,
    this.trailing,
  });

  final String title;
  final String? subtitle;
  final VoidCallback onBack;
  final Widget? trailing;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: const BoxDecoration(
        color: StitchColors.surface,
        border: Border(
          bottom: BorderSide(color: StitchColors.outlineVariant, width: 2),
        ),
      ),
      child: Row(
        children: [
          IconSquareButton(
            icon: StitchIcons.arrowBack,
            semanticLabel: 'Regresar',
            onTap: onBack,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: StitchText.headlineMd,
                ),
                if (subtitle != null && subtitle!.isNotEmpty)
                  Text(
                    subtitle!,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: StitchText.labelSm,
                  ),
              ],
            ),
          ),
          if (trailing != null) ...[
            const SizedBox(width: 8),
            trailing!,
          ],
        ],
      ),
    );
  }
}

class BottomYardNavBar extends StatelessWidget {
  const BottomYardNavBar({
    super.key,
    required this.currentTab,
    required this.onSelectTab,
    required this.onCameraTap,
  });

  final YardTab currentTab;
  final ValueChanged<YardTab> onSelectTab;
  final VoidCallback onCameraTap;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 6),
      decoration: const BoxDecoration(
        color: StitchColors.surfaceContainerLowest,
        border: Border(
          top: BorderSide(color: StitchColors.outline, width: 2),
        ),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Expanded(
            child: _NavTabButton(
              label: 'Inicio',
              icon: StitchIcons.home,
              selected: currentTab == YardTab.home,
              onTap: () => onSelectTab(YardTab.home),
            ),
          ),
          Expanded(
            child: _NavTabButton(
              label: 'Inventario',
              icon: StitchIcons.inventory2,
              selected: currentTab == YardTab.inventory,
              onTap: () => onSelectTab(YardTab.inventory),
            ),
          ),
          Expanded(
            child: GestureDetector(
              behavior: HitTestBehavior.opaque,
              onTap: onCameraTap,
              child: Transform.translate(
                offset: const Offset(0, -10),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 56,
                      height: 56,
                      alignment: Alignment.center,
                      decoration: BoxDecoration(
                        color: StitchColors.primary,
                        shape: BoxShape.circle,
                        border: Border.all(color: StitchColors.surface, width: 2),
                      ),
                      child: const Icon(
                        StitchIcons.photoCamera,
                        size: 28,
                        color: StitchColors.onPrimary,
                      ),
                    ),
                    const SizedBox(height: 2),
                    Text(
                      'Cámara',
                      style: StitchText.labelSm.copyWith(
                        color: StitchColors.primary,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          Expanded(
            child: _NavTabButton(
              label: 'Ventas',
              icon: StitchIcons.receiptLong,
              selected: currentTab == YardTab.sales,
              onTap: () => onSelectTab(YardTab.sales),
            ),
          ),
          Expanded(
            child: _NavTabButton(
              label: 'Equipo',
              icon: StitchIcons.group,
              selected: currentTab == YardTab.team,
              onTap: () => onSelectTab(YardTab.team),
            ),
          ),
        ],
      ),
    );
  }
}

class _NavTabButton extends StatelessWidget {
  const _NavTabButton({
    required this.label,
    required this.icon,
    required this.selected,
    required this.onTap,
  });

  final String label;
  final IconData icon;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final tint = selected ? StitchColors.primary : StitchColors.onSurfaceVariant;
    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: onTap,
      child: Container(
        constraints: const BoxConstraints(minHeight: 56),
        padding: const EdgeInsets.symmetric(vertical: 6),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, size: 24, color: tint),
            const SizedBox(height: 2),
            Text(
              label,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: StitchText.labelSm.copyWith(color: tint),
            ),
          ],
        ),
      ),
    );
  }
}

class StatusBadge extends StatelessWidget {
  const StatusBadge({
    super.key,
    required this.text,
    required this.backgroundColor,
    required this.textColor,
    this.borderColor,
    this.icon,
  });

  final String text;
  final Color backgroundColor;
  final Color textColor;
  final Color? borderColor;
  final IconData? icon;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(StitchRadius.sm),
        border: borderColor != null ? Border.all(color: borderColor!, width: 1) : null,
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (icon != null) ...[
            Icon(icon, size: 14, color: textColor),
            const SizedBox(width: 4),
          ],
          Text(text, style: StitchText.labelSm.copyWith(color: textColor)),
        ],
      ),
    );
  }
}

class MetricKpiCard extends StatelessWidget {
  const MetricKpiCard({
    super.key,
    required this.label,
    required this.value,
    required this.subtitle,
    required this.subtitleColor,
    required this.icon,
    required this.iconTint,
  });

  final String label;
  final String value;
  final String subtitle;
  final Color subtitleColor;
  final IconData icon;
  final Color iconTint;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: StitchColors.surfaceContainerLowest,
        borderRadius: BorderRadius.circular(StitchRadius.card),
        border: Border.all(color: StitchColors.outlineVariant, width: 2),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  label.toUpperCase(),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: StitchText.labelSm,
                ),
              ),
              Icon(icon, size: 18, color: iconTint),
            ],
          ),
          const SizedBox(height: 6),
          Text(value, style: StitchText.monoLg),
          const SizedBox(height: 2),
          Text(
            subtitle,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: StitchText.bodyMd.copyWith(color: subtitleColor),
          ),
        ],
      ),
    );
  }
}

class YardFilterChip extends StatelessWidget {
  const YardFilterChip({
    super.key,
    required this.label,
    required this.selected,
    required this.onTap,
    this.icon,
    this.minHeight = 44,
  });

  final String label;
  final bool selected;
  final VoidCallback onTap;
  final IconData? icon;
  final double minHeight;

  @override
  Widget build(BuildContext context) {
    final bg = selected ? StitchColors.secondary : StitchColors.surfaceContainerLowest;
    final fg = selected ? StitchColors.onSecondary : StitchColors.onSurface;
    final border = selected ? StitchColors.secondary : StitchColors.outlineVariant;

    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: onTap,
      child: Container(
        constraints: BoxConstraints(minHeight: minHeight),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        decoration: BoxDecoration(
          color: bg,
          borderRadius: BorderRadius.circular(StitchRadius.full),
          border: Border.all(color: border, width: 2),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (icon != null) ...[
              Icon(icon, size: 18, color: fg),
              const SizedBox(width: 6),
            ],
            Text(label, style: StitchText.labelLg.copyWith(color: fg)),
          ],
        ),
      ),
    );
  }
}

class PartActionCard extends StatelessWidget {
  const PartActionCard({
    super.key,
    required this.part,
    required this.onTapCard,
    this.actionLabel,
    this.onActionTap,
    this.isRestore = false,
  });

  final YardPart part;
  final VoidCallback onTapCard;
  final String? actionLabel;
  final VoidCallback? onActionTap;
  final bool isRestore;

  @override
  Widget build(BuildContext context) {
    final (statusText, statusBg, statusFg) = switch (part.status) {
      PartStatus.available => (
          'Disponible',
          StitchColors.successContainer,
          StitchColors.onSuccessContainer,
        ),
      PartStatus.inReview => (
          'En revisión',
          StitchColors.warningContainer,
          StitchColors.onWarningContainer,
        ),
      PartStatus.sold => (
          'Vendida',
          StitchColors.errorContainer,
          StitchColors.onErrorContainer,
        ),
    };

    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: onTapCard,
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: StitchColors.surfaceContainerLowest,
          borderRadius: BorderRadius.circular(StitchRadius.card),
          border: Border.all(
            color: part.status == PartStatus.sold
                ? StitchColors.outlineVariant
                : StitchColors.outline,
            width: 2,
          ),
        ),
        child: Column(
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  width: 88,
                  height: 88,
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: StitchColors.surfaceContainer,
                    borderRadius: BorderRadius.circular(StitchRadius.sm),
                    border: Border.all(color: StitchColors.outlineVariant, width: 1),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(
                        StitchIcons.build,
                        size: 28,
                        color: StitchColors.outline,
                      ),
                      const SizedBox(height: 4),
                      Text(part.reference, style: StitchText.monoMd),
                    ],
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          StatusBadge(
                            text: statusText,
                            backgroundColor: statusBg,
                            textColor: statusFg,
                          ),
                          const SizedBox(width: 8),
                          Expanded(
                            child: Text(
                              part.location,
                              textAlign: TextAlign.right,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              style: StitchText.monoMd,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 6),
                      Text(
                        part.name,
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                        style: StitchText.titleLg,
                      ),
                      const SizedBox(height: 2),
                      Text(
                        part.subtitle,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: StitchText.bodyMd,
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 10),
            Container(height: 1, color: StitchColors.outlineVariant),
            const SizedBox(height: 10),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Precio contado', style: StitchText.labelSm),
                    Text(formatMxn(part.priceMxn), style: StitchText.monoLg),
                  ],
                ),
                if (actionLabel != null && onActionTap != null)
                  GestureDetector(
                    behavior: HitTestBehavior.opaque,
                    onTap: onActionTap,
                    child: Container(
                      constraints: const BoxConstraints(minHeight: 48),
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      decoration: BoxDecoration(
                        color: isRestore
                            ? StitchColors.surfaceContainerLowest
                            : StitchColors.primary,
                        borderRadius: BorderRadius.circular(StitchRadius.md),
                        border: Border.all(
                          color: isRestore
                              ? StitchColors.secondary
                              : StitchColors.primary,
                          width: 2,
                        ),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(
                            isRestore ? StitchIcons.restore : StitchIcons.pointOfSale,
                            size: 20,
                            color: isRestore
                                ? StitchColors.secondary
                                : StitchColors.onPrimary,
                          ),
                          const SizedBox(width: 6),
                          Text(
                            actionLabel!,
                            style: StitchText.labelLg.copyWith(
                              color: isRestore
                                  ? StitchColors.secondary
                                  : StitchColors.onPrimary,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
