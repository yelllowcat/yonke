import 'package:flutter/widgets.dart';
import '../components/custom_input.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';
import 'part_detail_screen.dart';

/// Ventas tab: fast search of sellable parts (pushing PartDetailScreen) + sold history.
class SalesScreen extends StatefulWidget {
  const SalesScreen({super.key, required this.store});

  final YonkeStore store;

  @override
  State<SalesScreen> createState() => _SalesScreenState();
}

class _SalesScreenState extends State<SalesScreen> {
  final _queryController = TextEditingController();

  @override
  void dispose() {
    _queryController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: widget.store,
      builder: (context, _) {
        final needle = _queryController.text.trim().toLowerCase();
        final isOwner = widget.store.currentUser?.role == StaffRole.owner;
        final sellable = widget.store.parts.where((p) {
          if (p.status == PartStatus.sold) return false;
          if (needle.isEmpty) return true;
          return '${p.reference} ${p.name} ${p.subtitle} ${p.location}'
              .toLowerCase()
              .contains(needle);
        }).toList();
        final sold = widget.store.soldParts;

        return YardScaffold(
          store: widget.store,
          title: 'Ventas',
          currentTab: YardTab.sales,
          body: ListView(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 24),
            children: [
              CustomInput(
                controller: _queryController,
                label: 'Buscar pieza para vender',
                hint: 'Referencia #P-408, nombre o auto...',
                prefixIcon: StitchIcons.search,
                onChanged: (_) => setState(() {}),
              ),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      'Listas para cobro',
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: StitchText.titleLg,
                    ),
                  ),
                  const SizedBox(width: 8),
                  const Text('Venta rápida', style: StitchText.labelSm),
                ],
              ),
              const SizedBox(height: 12),
              for (final part in sellable) ...[
                PartActionCard(
                  part: part,
                  onTapCard: () => AppNavigator.push(
                    context,
                    PartDetailScreen(store: widget.store, partId: part.id),
                  ),
                  actionLabel: 'Vender',
                  onActionTap: () => AppNavigator.push(
                    context,
                    PartDetailScreen(store: widget.store, partId: part.id),
                  ),
                ),
                const SizedBox(height: 12),
              ],
              if (sold.isNotEmpty) ...[
                const SizedBox(height: 8),
                const Text('Vendidas en este turno', style: StitchText.titleLg),
                const SizedBox(height: 10),
                for (final part in sold) ...[
                  PartActionCard(
                    part: part,
                    onTapCard: () => AppNavigator.push(
                      context,
                      PartDetailScreen(store: widget.store, partId: part.id),
                    ),
                    actionLabel: isOwner ? 'Restaurar' : null,
                    isRestore: true,
                    onActionTap: isOwner
                        ? () => widget.store.restorePart(part.id)
                        : null,
                  ),
                  const SizedBox(height: 12),
                ],
              ],
            ],
          ),
        );
      },
    );
  }
}
