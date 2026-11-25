import 'package:flutter/widgets.dart';
import '../components/custom_button.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';

/// Screen 4: Detalle y Venta Rápida (Stitch 04_detalle_venta.html)
/// Pushed via `AppNavigator.push` and returns via `AppNavigator.pop(context)`.
class PartDetailScreen extends StatefulWidget {
  const PartDetailScreen({
    super.key,
    required this.store,
    required this.partId,
  });

  final YonkeStore store;
  final String partId;

  @override
  State<PartDetailScreen> createState() => _PartDetailScreenState();
}

class _PartDetailScreenState extends State<PartDetailScreen> {
  PaymentMethod _payment = PaymentMethod.cash;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: widget.store,
      builder: (context, _) {
        final part = widget.store.partById(widget.partId);
        if (part == null) {
          return ColoredBox(
            color: StitchColors.surface,
            child: SafeArea(
              child: Column(
                children: [
                  ContextualBackHeader(
                    title: 'Detalle de pieza',
                    onBack: () => AppNavigator.pop(context),
                  ),
                  const Expanded(
                    child: Center(
                      child: Text('Pieza no encontrada.', style: StitchText.titleLg),
                    ),
                  ),
                ],
              ),
            ),
          );
        }

        final vehicle = widget.store.vehicleById(part.vehicleId);
        final isSold = part.status == PartStatus.sold;
        final isOwner = widget.store.currentUser?.role == StaffRole.owner;
        final priceText = formatMxn(part.priceMxn);

        final (statusText, statusBg, statusFg, statusBorder) = switch (part.status) {
          PartStatus.available => (
              'Disponible',
              StitchColors.successContainer,
              StitchColors.onSuccessContainer,
              StitchColors.success,
            ),
          PartStatus.inReview => (
              'En revisión',
              StitchColors.warningContainer,
              StitchColors.onWarningContainer,
              StitchColors.warning,
            ),
          PartStatus.sold => (
              'Vendida',
              StitchColors.errorContainer,
              StitchColors.onErrorContainer,
              StitchColors.error,
            ),
        };

        return ColoredBox(
          color: StitchColors.surface,
          child: SafeArea(
            child: Column(
              children: [
                ContextualBackHeader(
                  title: 'Pieza #${part.reference}',
                  subtitle: part.location,
                  onBack: () => AppNavigator.pop(context),
                  trailing: StatusBadge(
                    text: statusText,
                    backgroundColor: statusBg,
                    textColor: statusFg,
                    borderColor: statusBorder,
                  ),
                ),
                Expanded(
                  child: ListView(
                    padding: const EdgeInsets.all(16),
                    children: [
                      // 4:3 Part Photo Card
                      AspectRatio(
                        aspectRatio: 4 / 3,
                        child: Container(
                          decoration: BoxDecoration(
                            color: StitchColors.surfaceContainer,
                            borderRadius: BorderRadius.circular(StitchRadius.card),
                            border: Border.all(
                              color: StitchColors.outline,
                              width: 2,
                            ),
                          ),
                          child: Stack(
                            children: [
                              Center(
                                child: Column(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    const Icon(
                                      StitchIcons.build,
                                      size: 48,
                                      color: StitchColors.outline,
                                    ),
                                    const SizedBox(height: 6),
                                    Text(
                                      '${part.reference} · ${part.photoCount} foto(s)',
                                      style: StitchText.monoMd,
                                    ),
                                  ],
                                ),
                              ),
                              Positioned(
                                right: 12,
                                bottom: 12,
                                child: GestureDetector(
                                  behavior: HitTestBehavior.opaque,
                                  onTap: () => widget.store.incrementPartPhoto(part.id),
                                  child: Container(
                                    constraints: const BoxConstraints(minHeight: 44),
                                    padding: const EdgeInsets.symmetric(
                                      horizontal: 12,
                                      vertical: 8,
                                    ),
                                    decoration: BoxDecoration(
                                      color: StitchColors.surfaceContainerLowest,
                                      borderRadius: BorderRadius.circular(StitchRadius.md),
                                      border: Border.all(
                                        color: StitchColors.outline,
                                        width: 2,
                                      ),
                                    ),
                                    child: const Row(
                                      mainAxisSize: MainAxisSize.min,
                                      children: [
                                        Icon(
                                          StitchIcons.addAPhoto,
                                          size: 18,
                                          color: StitchColors.onSurface,
                                        ),
                                        SizedBox(width: 6),
                                        Text('Agregar foto', style: StitchText.labelSm),
                                      ],
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      const SizedBox(height: 16),

                      // Title & Vehicle Metadata Card
                      Text(part.name, style: StitchText.headlineLgMobile),
                      const SizedBox(height: 10),
                      Container(
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: StitchColors.surfaceContainerLowest,
                          borderRadius: BorderRadius.circular(StitchRadius.card),
                          border: Border.all(
                            color: StitchColors.outlineVariant,
                            width: 2,
                          ),
                        ),
                        child: Row(
                          children: [
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text('Vehículo', style: StitchText.labelSm),
                                  const SizedBox(height: 2),
                                  Text(
                                    vehicle?.displayName ?? part.subtitle,
                                    style: StitchText.titleMd,
                                  ),
                                ],
                              ),
                            ),
                            const SizedBox(width: 12),
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text('Categoría', style: StitchText.labelSm),
                                  const SizedBox(height: 2),
                                  Text(part.category, style: StitchText.titleMd),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 16),

                      // Price & Payment Method Box
                      Container(
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: StitchColors.surfaceContainerLowest,
                          borderRadius: BorderRadius.circular(StitchRadius.card),
                          border: Border.all(
                            color: StitchColors.outline,
                            width: 2,
                          ),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              crossAxisAlignment: CrossAxisAlignment.baseline,
                              textBaseline: TextBaseline.alphabetic,
                              children: [
                                Expanded(
                                  child: Text(
                                    'Total a cobrar',
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                    style: StitchText.labelLg.copyWith(
                                      color: StitchColors.onSurfaceVariant,
                                    ),
                                  ),
                                ),
                                const SizedBox(width: 8),
                                Text(priceText, style: StitchText.monoDisplay),
                              ],
                            ),
                            if (!isSold) ...[
                              const SizedBox(height: 16),
                              const Text('Forma de pago', style: StitchText.labelSm),
                              const SizedBox(height: 8),
                              Row(
                                children: [
                                  for (final entry in const [
                                    (PaymentMethod.cash, 'Efectivo'),
                                    (PaymentMethod.transfer, 'Transferencia'),
                                    (PaymentMethod.card, 'Tarjeta'),
                                  ]) ...[
                                    if (entry.$1 != PaymentMethod.cash)
                                      const SizedBox(width: 8),
                                    Expanded(
                                      child: _PaymentChip(
                                        label: entry.$2,
                                        selected: _payment == entry.$1,
                                        onTap: () => setState(() => _payment = entry.$1),
                                      ),
                                    ),
                                  ],
                                ],
                              ),
                            ],
                          ],
                        ),
                      ),
                    ],
                  ),
                ),

                // Pinned Atomic Sale Action Footer
                Container(
                  padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
                  decoration: const BoxDecoration(
                    color: StitchColors.surface,
                    border: Border(
                      top: BorderSide(color: StitchColors.outlineVariant, width: 2),
                    ),
                  ),
                  child: !isSold
                      ? CustomButton(
                          text: part.priceMxn != null
                              ? 'Confirmar venta · $priceText'
                              : 'Confirmar venta',
                          icon: StitchIcons.pointOfSale,
                          minHeight: 56,
                          onTap: () {
                            widget.store.sellPart(part.id, _payment);
                            AppNavigator.pop(context);
                          },
                        )
                      : isOwner
                          ? CustomButton(
                              text: 'Restaurar al inventario',
                              icon: StitchIcons.restore,
                              isSecondary: true,
                              minHeight: 54,
                              onTap: () => widget.store.restorePart(part.id),
                            )
                          : CustomButton(
                              text: 'Regresar al inventario',
                              isSecondary: true,
                              subtleBorder: true,
                              onTap: () => AppNavigator.pop(context),
                            ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

class _PaymentChip extends StatelessWidget {
  const _PaymentChip({
    required this.label,
    required this.selected,
    required this.onTap,
  });

  final String label;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: onTap,
      child: Container(
        constraints: const BoxConstraints(minHeight: 48),
        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 10),
        alignment: Alignment.center,
        decoration: BoxDecoration(
          color: selected
              ? StitchColors.secondary
              : StitchColors.surfaceContainerLowest,
          borderRadius: BorderRadius.circular(StitchRadius.md),
          border: Border.all(
            color: selected
                ? StitchColors.secondary
                : StitchColors.outlineVariant,
            width: 2,
          ),
        ),
        child: Text(
          label,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: StitchText.labelSm.copyWith(
            color: selected ? StitchColors.onSecondary : StitchColors.onSurface,
          ),
        ),
      ),
    );
  }
}
