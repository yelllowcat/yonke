import 'package:flutter/widgets.dart';
import '../components/custom_button.dart';
import '../components/custom_input.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';

/// Screen 3: Cámara e Ingreso (Stitch 03_camara_ingreso.html)
/// Pushed via `AppNavigator.push` and dismissed via `AppNavigator.pop(context)`.
class CameraIntakeScreen extends StatefulWidget {
  const CameraIntakeScreen({
    super.key,
    required this.store,
    this.initialVehicleMode = false,
  });

  final YonkeStore store;
  final bool initialVehicleMode;

  @override
  State<CameraIntakeScreen> createState() => _CameraIntakeScreenState();
}

class _CameraIntakeScreenState extends State<CameraIntakeScreen> {
  late bool _vehicleMode;
  bool _flashOn = false;
  bool _shutterFlash = false;
  int _photoCount = 2;

  late String _selectedVehicleId;
  String _selectedCategory = 'Motor';
  String? _error;

  final _partNameController = TextEditingController();
  final _partLocationController = TextEditingController();
  final _partPriceController = TextEditingController();

  final _vehicleYearController = TextEditingController();
  final _vehicleMakeController = TextEditingController();
  final _vehicleModelController = TextEditingController();
  final _vehicleLocationController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _vehicleMode = widget.initialVehicleMode;
    _selectedVehicleId = widget.store.vehicles.first.id;
  }

  @override
  void dispose() {
    _partNameController.dispose();
    _partLocationController.dispose();
    _partPriceController.dispose();
    _vehicleYearController.dispose();
    _vehicleMakeController.dispose();
    _vehicleModelController.dispose();
    _vehicleLocationController.dispose();
    super.dispose();
  }

  void _takePhoto() {
    setState(() {
      _shutterFlash = true;
      if (_photoCount < 4) _photoCount++;
    });
    Future<void>.delayed(const Duration(milliseconds: 120), () {
      if (mounted) setState(() => _shutterFlash = false);
    });
  }

  void _save() {
    if (_vehicleMode) {
      final year = int.tryParse(_vehicleYearController.text.trim()) ?? 0;
      final ok = widget.store.addVehicle(
        year: year,
        make: _vehicleMakeController.text,
        model: _vehicleModelController.text,
        location: _vehicleLocationController.text,
      );
      if (!ok) {
        setState(() => _error = 'Escribe año (4 dígitos), marca y modelo.');
        return;
      }
      AppNavigator.pop(context);
      return;
    }

    final ok = widget.store.addPart(
      vehicleId: _selectedVehicleId,
      name: _partNameController.text.trim().isEmpty
          ? '$_selectedCategory de patio'
          : _partNameController.text,
      category: _selectedCategory,
      location: _partLocationController.text,
      priceMxn: int.tryParse(_partPriceController.text.replaceAll(RegExp(r'\D'), '')),
      photoCount: _photoCount,
    );
    if (!ok) {
      setState(() => _error = 'Revisa los datos de la pieza antes de guardar.');
      return;
    }
    AppNavigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    final vehicles = widget.store.vehicles;

    return ColoredBox(
      color: StitchColors.inverseSurface,
      child: SafeArea(
        child: Column(
          children: [
            // Dark Inverse Top Camera Controls
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: const BoxDecoration(
                color: StitchColors.inverseSurface,
                border: Border(
                  bottom: BorderSide(color: StitchColors.outline, width: 1),
                ),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  IconSquareButton(
                    icon: StitchIcons.arrowBack,
                    semanticLabel: 'Regresar al inventario',
                    onTap: () => AppNavigator.pop(context),
                  ),
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8),
                      child: Column(
                        children: [
                          Text(
                            _vehicleMode ? 'Ingreso de vehículo' : 'Ingreso de pieza',
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: StitchText.titleMd.copyWith(
                              color: StitchColors.inverseOnSurface,
                            ),
                          ),
                          Text(
                            'Cola sin conexión lista',
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: StitchText.labelSm.copyWith(
                              color: StitchColors.primaryContainer,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  IconSquareButton(
                    icon: _flashOn ? StitchIcons.flashOn : StitchIcons.flashAuto,
                    semanticLabel: 'Alternar flash',
                    onTap: () => setState(() => _flashOn = !_flashOn),
                  ),
                ],
              ),
            ),

            Expanded(
              child: Container(
                color: StitchColors.surface,
                child: ListView(
                  padding: EdgeInsets.zero,
                  children: [
                    // Live Viewfinder Area with Corner Guides
                    GestureDetector(
                      onTap: _takePhoto,
                      child: Container(
                        height: 210,
                        width: double.infinity,
                        color: StitchColors.inverseSurface,
                        child: Stack(
                          children: [
                            Center(
                              child: Column(
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  const Icon(
                                    StitchIcons.photoCamera,
                                    size: 46,
                                    color: StitchColors.primaryContainer,
                                  ),
                                  const SizedBox(height: 8),
                                  Text(
                                    'Toca para capturar foto ($_photoCount / 4)',
                                    style: StitchText.labelLg.copyWith(
                                      color: StitchColors.inverseOnSurface,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            const Positioned.fill(
                              child: Padding(
                                padding: EdgeInsets.all(20),
                                child: _ViewfinderCorners(),
                              ),
                            ),
                            Align(
                              alignment: Alignment.topCenter,
                              child: Padding(
                                padding: const EdgeInsets.only(top: 12),
                                child: Container(
                                  padding: const EdgeInsets.symmetric(
                                    horizontal: 12,
                                    vertical: 4,
                                  ),
                                  decoration: BoxDecoration(
                                    color: StitchColors.inverseSurface.withValues(alpha: 0.9),
                                    borderRadius: BorderRadius.circular(StitchRadius.full),
                                    border: Border.all(
                                      color: StitchColors.outline,
                                      width: 1,
                                    ),
                                  ),
                                  child: Text(
                                    'Enfoca la pieza o placa',
                                    style: StitchText.labelSm.copyWith(
                                      color: StitchColors.inverseOnSurface,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                            if (_shutterFlash)
                              Positioned.fill(
                                child: ColoredBox(
                                  color: StitchColors.surfaceContainerLowest
                                      .withValues(alpha: 0.8),
                                ),
                              ),
                          ],
                        ),
                      ),
                    ),
                    Container(height: 2, color: StitchColors.outline),

                    // Lower Light Panel: Thumbnails + Quick Tags + Form + Actions
                    Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          // Thumbnail Strip
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Row(
                                children: [
                                  for (var i = 0; i < _photoCount; i++) ...[
                                    Container(
                                      width: 52,
                                      height: 52,
                                      alignment: Alignment.center,
                                      decoration: BoxDecoration(
                                        color: StitchColors.surfaceContainer,
                                        borderRadius: BorderRadius.circular(StitchRadius.sm),
                                        border: Border.all(
                                          color: i == _photoCount - 1
                                              ? StitchColors.primary
                                              : StitchColors.outlineVariant,
                                          width: 2,
                                        ),
                                      ),
                                      child: Text(
                                        'F${i + 1}',
                                        style: StitchText.monoMd,
                                      ),
                                    ),
                                    const SizedBox(width: 8),
                                  ],
                                  if (_photoCount < 4)
                                    GestureDetector(
                                      onTap: _takePhoto,
                                      child: Container(
                                        width: 52,
                                        height: 52,
                                        alignment: Alignment.center,
                                        decoration: BoxDecoration(
                                          color: StitchColors.surfaceContainerLow,
                                          borderRadius: BorderRadius.circular(StitchRadius.sm),
                                          border: Border.all(
                                            color: StitchColors.outline,
                                            width: 2,
                                          ),
                                        ),
                                        child: const Icon(
                                          StitchIcons.addAPhoto,
                                          size: 20,
                                          color: StitchColors.outline,
                                        ),
                                      ),
                                    ),
                                ],
                              ),
                              Text(
                                '$_photoCount / 4 fotos',
                                style: StitchText.monoMd.copyWith(
                                  color: StitchColors.onSurface,
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 14),

                          // Mode Toggle (Pieza / Vehículo)
                          Row(
                            children: [
                              Expanded(
                                child: YardFilterChip(
                                  label: 'Pieza',
                                  icon: StitchIcons.build,
                                  selected: !_vehicleMode,
                                  onTap: () => setState(() {
                                    _vehicleMode = false;
                                    _error = null;
                                  }),
                                ),
                              ),
                              const SizedBox(width: 8),
                              Expanded(
                                child: YardFilterChip(
                                  label: 'Vehículo',
                                  icon: StitchIcons.directionsCar,
                                  selected: _vehicleMode,
                                  onTap: () => setState(() {
                                    _vehicleMode = true;
                                    _error = null;
                                  }),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 14),

                          // 1-Tap Quick Tags (Zero Typing)
                          if (!_vehicleMode) ...[
                            const Text(
                              'Etiquetas rápidas (toca para asignar)',
                              style: StitchText.labelSm,
                            ),
                            const SizedBox(height: 8),
                            Wrap(
                              spacing: 8,
                              runSpacing: 8,
                              children: [
                                for (final v in vehicles.take(3))
                                  YardFilterChip(
                                    label: v.displayName,
                                    selected: _selectedVehicleId == v.id,
                                    minHeight: 40,
                                    onTap: () => setState(() => _selectedVehicleId = v.id),
                                  ),
                                for (final cat in const ['Motor', 'Eléctrico', 'Carrocería'])
                                  YardFilterChip(
                                    label: cat,
                                    selected: _selectedCategory == cat,
                                    minHeight: 40,
                                    onTap: () => setState(() => _selectedCategory = cat),
                                  ),
                              ],
                            ),
                            const SizedBox(height: 16),
                            CustomInput(
                              controller: _partNameController,
                              label: 'Nombre de la pieza',
                              hint: 'Ej. Alternador Bosch 110A',
                            ),
                            const SizedBox(height: 12),
                            Row(
                              children: [
                                Expanded(
                                  child: CustomInput(
                                    controller: _partLocationController,
                                    label: 'Ubicación',
                                    hint: 'Pasillo B-04',
                                  ),
                                ),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: CustomInput(
                                    controller: _partPriceController,
                                    label: 'Precio MXN',
                                    hint: '1450',
                                    keyboardType: TextInputType.number,
                                  ),
                                ),
                              ],
                            ),
                          ] else ...[
                            CustomInput(
                              controller: _vehicleYearController,
                              label: 'Año',
                              hint: '2014',
                              keyboardType: TextInputType.number,
                            ),
                            const SizedBox(height: 12),
                            Row(
                              children: [
                                Expanded(
                                  child: CustomInput(
                                    controller: _vehicleMakeController,
                                    label: 'Marca',
                                    hint: 'Nissan',
                                  ),
                                ),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: CustomInput(
                                    controller: _vehicleModelController,
                                    label: 'Modelo',
                                    hint: 'Tsuru',
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 12),
                            CustomInput(
                              controller: _vehicleLocationController,
                              label: 'Ubicación en patio',
                              hint: 'Pasillo B-04',
                            ),
                          ],

                          if (_error != null) ...[
                            const SizedBox(height: 12),
                            Container(
                              padding: const EdgeInsets.all(10),
                              decoration: BoxDecoration(
                                color: StitchColors.errorContainer,
                                borderRadius: BorderRadius.circular(StitchRadius.card),
                                border: Border.all(color: StitchColors.error, width: 2),
                              ),
                              child: Text(
                                _error!,
                                style: StitchText.bodyMd.copyWith(
                                  color: StitchColors.onErrorContainer,
                                ),
                              ),
                            ),
                          ],
                          const SizedBox(height: 18),

                          // Shutter + Save Actions (2-column Stitch bar)
                          Row(
                            children: [
                              Expanded(
                                child: CustomButton(
                                  text: 'Tomar foto',
                                  icon: StitchIcons.photoCamera,
                                  minHeight: 56,
                                  onTap: _takePhoto,
                                ),
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: CustomButton(
                                  text: 'Guardar ($_photoCount)',
                                  icon: StitchIcons.cloudUpload,
                                  isSecondary: true,
                                  minHeight: 56,
                                  onTap: _save,
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ViewfinderCorners extends StatelessWidget {
  const _ViewfinderCorners();

  @override
  Widget build(BuildContext context) {
    const color = StitchColors.primaryContainer;
    const len = 28.0;
    const stroke = 4.0;
    return Stack(
      children: [
        // Top-left
        Positioned(top: 0, left: 0, child: Container(width: len, height: stroke, color: color)),
        Positioned(top: 0, left: 0, child: Container(width: stroke, height: len, color: color)),
        // Top-right
        Positioned(top: 0, right: 0, child: Container(width: len, height: stroke, color: color)),
        Positioned(top: 0, right: 0, child: Container(width: stroke, height: len, color: color)),
        // Bottom-left
        Positioned(bottom: 0, left: 0, child: Container(width: len, height: stroke, color: color)),
        Positioned(bottom: 0, left: 0, child: Container(width: stroke, height: len, color: color)),
        // Bottom-right
        Positioned(bottom: 0, right: 0, child: Container(width: len, height: stroke, color: color)),
        Positioned(bottom: 0, right: 0, child: Container(width: stroke, height: len, color: color)),
      ],
    );
  }
}
