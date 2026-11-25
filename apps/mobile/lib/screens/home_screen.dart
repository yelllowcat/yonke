import 'package:flutter/widgets.dart';
import '../components/custom_button.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';
import 'camera_intake_screen.dart';
import 'inventory_screen.dart';
import 'sales_screen.dart';
import 'team_owner_screen.dart';

/// Inicio tab with large high-contrast yard actions and `Navigator.popUntil(isFirst)` sign-out.
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key, required this.store});

  final YonkeStore store;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: store,
      builder: (context, _) {
        final user = store.currentUser;
        return YardScaffold(
          store: store,
          title: 'Yonke Fácil',
          currentTab: YardTab.home,
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'Hola, ${user?.name ?? "Equipo"}',
                  style: StitchText.titleMd.copyWith(color: StitchColors.secondary),
                ),
                const SizedBox(height: 4),
                const Text('¿Qué necesitas hacer?', style: StitchText.headlineLgMobile),
                const SizedBox(height: 16),

                // KPI Strip
                Row(
                  children: [
                    Expanded(
                      child: MetricKpiCard(
                        label: 'Piezas activas',
                        value: '${store.activePartsCount}',
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
                        value: '${store.vehicles.length}',
                        subtitle: '${store.pendingReviewCount} en revisión',
                        subtitleColor: StitchColors.warning,
                        icon: StitchIcons.directionsCar,
                        iconTint: StitchColors.secondary,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),

                // Highlighted Stitch Interactive Action Card
                _YardActionCard(
                  title: 'Ingresar vehículo nuevo',
                  subtitle: 'Abrir cámara y tomar fotos en patio',
                  icon: StitchIcons.photoCamera,
                  highlighted: true,
                  onTap: () => AppNavigator.push(
                    context,
                    CameraIntakeScreen(store: store, initialVehicleMode: true),
                  ),
                ),
                const SizedBox(height: 12),
                _YardActionCard(
                  title: 'Agregar pieza desmontada',
                  subtitle: 'Tomar foto y asignar pasillo o precio',
                  icon: StitchIcons.build,
                  onTap: () => AppNavigator.push(
                    context,
                    CameraIntakeScreen(store: store, initialVehicleMode: false),
                  ),
                ),
                const SizedBox(height: 12),
                _YardActionCard(
                  title: 'Buscar en inventario',
                  subtitle: 'Filtrar por marca, categoría o ubicación',
                  icon: StitchIcons.search,
                  onTap: () => AppNavigator.pushReplacement(
                    context,
                    InventoryScreen(store: store),
                  ),
                ),
                const SizedBox(height: 12),
                _YardActionCard(
                  title: 'Venta rápida',
                  subtitle: 'Cobrar y descontar pieza al instante',
                  icon: StitchIcons.pointOfSale,
                  onTap: () => AppNavigator.pushReplacement(
                    context,
                    SalesScreen(store: store),
                  ),
                ),
                if (user?.role == StaffRole.owner) ...[
                  const SizedBox(height: 12),
                  _YardActionCard(
                    title: 'Equipo y Dueño',
                    subtitle: 'Códigos de enlace, personal y recuperación',
                    icon: StitchIcons.adminPanelSettings,
                    onTap: () => AppNavigator.pushReplacement(
                      context,
                      TeamOwnerScreen(store: store),
                    ),
                  ),
                ],
                const SizedBox(height: 20),

                // Sign-out matching `eventhub-flutter/lib/screens/home_screen.dart`
                CustomButton(
                  text: 'Cerrar sesión',
                  icon: StitchIcons.logout,
                  backgroundColor: StitchColors.error,
                  onTap: () {
                    store.signOut();
                    AppNavigator.popToLogin(context);
                  },
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

class _YardActionCard extends StatelessWidget {
  const _YardActionCard({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.onTap,
    this.highlighted = false,
  });

  final String title;
  final String subtitle;
  final IconData icon;
  final VoidCallback onTap;
  final bool highlighted;

  @override
  Widget build(BuildContext context) {
    final bg = highlighted
        ? StitchColors.primaryContainer
        : StitchColors.surfaceContainerLowest;
    final border = highlighted ? StitchColors.primary : StitchColors.outlineVariant;
    final titleColor = highlighted
        ? StitchColors.onPrimaryContainer
        : StitchColors.onSurface;
    final subColor = highlighted
        ? StitchColors.onPrimaryContainer
        : StitchColors.onSurfaceVariant;

    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: bg,
          borderRadius: BorderRadius.circular(StitchRadius.card),
          border: Border.all(color: border, width: 2),
        ),
        child: Row(
          children: [
            Container(
              width: 48,
              height: 48,
              alignment: Alignment.center,
              decoration: BoxDecoration(
                color: highlighted
                    ? StitchColors.primary
                    : StitchColors.surfaceContainer,
                borderRadius: BorderRadius.circular(StitchRadius.md),
              ),
              child: Icon(
                icon,
                size: 26,
                color: highlighted
                    ? StitchColors.onPrimary
                    : StitchColors.secondary,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(title, style: StitchText.titleLg.copyWith(color: titleColor)),
                  const SizedBox(height: 2),
                  Text(subtitle, style: StitchText.bodyMd.copyWith(color: subColor)),
                ],
              ),
            ),
            Icon(
              StitchIcons.chevronRight,
              size: 24,
              color: highlighted ? StitchColors.primary : StitchColors.outline,
            ),
          ],
        ),
      ),
    );
  }
}
