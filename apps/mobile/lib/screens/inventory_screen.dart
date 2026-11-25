import 'package:flutter/widgets.dart';
import '../components/custom_input.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';
import 'part_detail_screen.dart';

/// Screen 2: Inventario y Patio (Stitch 02_inventario_patio.html)
class InventoryScreen extends StatefulWidget {
  const InventoryScreen({super.key, required this.store});

  final YonkeStore store;

  @override
  State<InventoryScreen> createState() => _InventoryScreenState();
}

class _InventoryScreenState extends State<InventoryScreen> {
  final _searchController = TextEditingController();
  String _selectedCategory = '';

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: widget.store,
      builder: (context, _) {
        final query = _searchController.text.trim().toLowerCase();
        final allParts = widget.store.parts;
        final categories = <String>{
          'Motor',
          'Eléctrico',
          'Carrocería',
          ...allParts.map((p) => p.category),
        }.toList();

        final filtered = allParts.where((part) {
          final matchesCategory = _selectedCategory.isEmpty ||
              part.category.toLowerCase() == _selectedCategory.toLowerCase();
          final matchesQuery = query.isEmpty ||
              '${part.name} ${part.subtitle} ${part.reference} ${part.location} ${part.category}'
                  .toLowerCase()
                  .contains(query);
          return matchesCategory && matchesQuery;
        }).toList()
          ..sort((a, b) {
            final aSold = a.status == PartStatus.sold ? 1 : 0;
            final bSold = b.status == PartStatus.sold ? 1 : 0;
            return aSold.compareTo(bSold);
          });

        return YardScaffold(
          store: widget.store,
          title: 'Inventario',
          currentTab: YardTab.inventory,
          body: ListView(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 24),
            children: [
              // KPI Strip
              Row(
                children: [
                  Expanded(
                    child: MetricKpiCard(
                      label: 'Piezas activas',
                      value: '${widget.store.activePartsCount}',
                      subtitle: 'Listas para venta',
                      subtitleColor: StitchColors.success,
                      icon: StitchIcons.inventory2,
                      iconTint: StitchColors.primary,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: MetricKpiCard(
                      label: 'Autos en patio',
                      value: '${widget.store.vehicles.length}',
                      subtitle: '${widget.store.pendingReviewCount} en revisión',
                      subtitleColor: StitchColors.warning,
                      icon: StitchIcons.directionsCar,
                      iconTint: StitchColors.secondary,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),

              // Search Field
              CustomInput(
                controller: _searchController,
                hint: 'Buscar pieza, marca o año...',
                prefixIcon: StitchIcons.search,
                suffixWidget: _searchController.text.isNotEmpty
                    ? GestureDetector(
                        onTap: () => setState(() => _searchController.clear()),
                        child: const Icon(
                          StitchIcons.close,
                          size: 20,
                          color: StitchColors.outline,
                        ),
                      )
                    : null,
                onChanged: (_) => setState(() {}),
              ),
              const SizedBox(height: 12),

              // Horizontal Filter Chips
              SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: [
                    YardFilterChip(
                      label: 'Todo (${allParts.length})',
                      selected: _selectedCategory.isEmpty,
                      onTap: () => setState(() => _selectedCategory = ''),
                    ),
                    for (final cat in categories) ...[
                      const SizedBox(width: 8),
                      YardFilterChip(
                        label: cat,
                        selected: _selectedCategory == cat,
                        onTap: () => setState(
                          () => _selectedCategory =
                              _selectedCategory == cat ? '' : cat,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
              const SizedBox(height: 16),

              // Recent Parts Header
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      'Piezas recientes',
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: StitchText.titleLg,
                    ),
                  ),
                  const SizedBox(width: 8),
                  const Text('Toca para vender', style: StitchText.labelSm),
                ],
              ),
              const SizedBox(height: 12),

              if (filtered.isEmpty)
                Container(
                  padding: const EdgeInsets.all(24),
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: StitchColors.surfaceContainerLowest,
                    borderRadius: BorderRadius.circular(StitchRadius.card),
                    border: Border.all(
                      color: StitchColors.outlineVariant,
                      width: 2,
                    ),
                  ),
                  child: const Text(
                    'No encontramos piezas con ese filtro.',
                    style: StitchText.titleMd,
                  ),
                )
              else
                for (final part in filtered) ...[
                  PartActionCard(
                    part: part,
                    onTapCard: () => AppNavigator.push(
                      context,
                      PartDetailScreen(store: widget.store, partId: part.id),
                    ),
                    actionLabel: part.status == PartStatus.sold ? null : 'Vender',
                    onActionTap: part.status == PartStatus.sold
                        ? null
                        : () => AppNavigator.push(
                              context,
                              PartDetailScreen(
                                store: widget.store,
                                partId: part.id,
                              ),
                            ),
                  ),
                  const SizedBox(height: 12),
                ],
            ],
          ),
        );
      },
    );
  }
}
