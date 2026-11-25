import 'package:camera/camera.dart';
import 'package:flutter/widgets.dart';
import '../components/custom_button.dart';
import '../components/custom_input.dart';
import '../components/part_photo.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../services/photo_capture_service.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';

/// Screen 3: Cámara e Ingreso (Stitch 03_camara_ingreso.html)
/// Uses live `CameraController` + `CameraPreview` on devices with a camera,
/// with automatic `image_picker` fallback, and uploads photos to Firebase Storage + Firestore.
class CameraIntakeScreen extends StatefulWidget {
  const CameraIntakeScreen({
    super.key,
    required this.store,
    this.initialVehicleMode = false,
    this.captureService,
  });

  final YonkeStore store;
  final bool initialVehicleMode;
  final PhotoCaptureService? captureService;

  @override
  State<CameraIntakeScreen> createState() => _CameraIntakeScreenState();
}

class _CameraIntakeScreenState extends State<CameraIntakeScreen>
    with WidgetsBindingObserver {
  late final PhotoCaptureService _captureService;
  CameraController? _cameraController;
  bool _cameraReady = false;
  String? _cameraStatusNote;

  late bool _vehicleMode;
  bool _flashOn = false;
  bool _shutterFlash = false;
  bool _saving = false;
  final List<String> _photoPaths = [];

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
    WidgetsBinding.instance.addObserver(this);
    _captureService = widget.captureService ?? PhotoCaptureService();
    _vehicleMode = widget.initialVehicleMode;
    _selectedVehicleId = widget.store.vehicles.isNotEmpty
        ? widget.store.vehicles.first.id
        : '';
    _initLiveCamera();
  }

  Future<void> _initLiveCamera() async {
    try {
      final cameras = await availableCameras();
      if (cameras.isEmpty) {
        if (mounted) {
          setState(() {
            _cameraReady = false;
            _cameraStatusNote = 'Toca "Tomar foto" para abrir cámara o galería';
          });
        }
        return;
      }
      final backCamera = cameras.firstWhere(
        (c) => c.lensDirection == CameraLensDirection.back,
        orElse: () => cameras.first,
      );
      final controller = CameraController(
        backCamera,
        ResolutionPreset.medium,
        enableAudio: false,
      );
      await controller.initialize();
      await controller.setFlashMode(FlashMode.auto);
      if (!mounted) {
        await controller.dispose();
        return;
      }
      setState(() {
        _cameraController = controller;
        _cameraReady = true;
        _cameraStatusNote = null;
      });
    } catch (_) {
      if (mounted) {
        setState(() {
          _cameraReady = false;
          _cameraStatusNote = 'Toca "Tomar foto" para capturar o elegir imagen';
        });
      }
    }
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    final controller = _cameraController;
    if (controller == null || !controller.value.isInitialized) return;
    if (state == AppLifecycleState.inactive) {
      controller.dispose();
      _cameraController = null;
      if (mounted) setState(() => _cameraReady = false);
    } else if (state == AppLifecycleState.resumed) {
      _initLiveCamera();
    }
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _cameraController?.dispose();
    _partNameController.dispose();
    _partLocationController.dispose();
    _partPriceController.dispose();
    _vehicleYearController.dispose();
    _vehicleMakeController.dispose();
    _vehicleModelController.dispose();
    _vehicleLocationController.dispose();
    super.dispose();
  }

  Future<void> _toggleFlash() async {
    final next = !_flashOn;
    setState(() => _flashOn = next);
    final controller = _cameraController;
    if (controller != null && controller.value.isInitialized) {
      try {
        await controller.setFlashMode(next ? FlashMode.torch : FlashMode.auto);
      } catch (_) {}
    }
  }

  Future<void> _takePhoto() async {
    if (_photoPaths.length >= 4) {
      setState(() => _error = 'Máximo 4 fotos por registro.');
      return;
    }

    String? capturedPath;
    final controller = _cameraController;
    if (controller != null &&
        controller.value.isInitialized &&
        !controller.value.isTakingPicture) {
      try {
        setState(() => _shutterFlash = true);
        final shot = await controller.takePicture();
        capturedPath = shot.path;
      } catch (_) {
        capturedPath = null;
      } finally {
        Future<void>.delayed(const Duration(milliseconds: 120), () {
          if (mounted) setState(() => _shutterFlash = false);
        });
      }
    } else {
      capturedPath = await _captureService.captureOrPick();
    }

    if (!mounted) return;
    if (capturedPath != null && capturedPath.isNotEmpty) {
      setState(() {
        _photoPaths.add(capturedPath!);
        _error = null;
      });
    }
  }

  void _removePhotoAt(int index) {
    setState(() {
      _photoPaths.removeAt(index);
      _error = null;
    });
  }

  Future<void> _save() async {
    if (_saving) return;
    setState(() {
      _saving = true;
      _error = null;
    });

    if (_vehicleMode) {
      final year = int.tryParse(_vehicleYearController.text.trim()) ?? 0;
      final ok = await widget.store.addVehicle(
        year: year,
        make: _vehicleMakeController.text,
        model: _vehicleModelController.text,
        location: _vehicleLocationController.text,
        photoPaths: List<String>.from(_photoPaths),
      );
      if (!mounted) return;
      if (!ok) {
        setState(() {
          _saving = false;
          _error = 'Escribe año (4 dígitos), marca y modelo.';
        });
        return;
      }
      AppNavigator.pop(context);
      return;
    }

    final ok = await widget.store.addPart(
      vehicleId: _selectedVehicleId,
      name: _partNameController.text.trim().isEmpty
          ? '$_selectedCategory de patio'
          : _partNameController.text,
      category: _selectedCategory,
      location: _partLocationController.text,
      priceMxn: int.tryParse(
        _partPriceController.text.replaceAll(RegExp(r'\D'), ''),
      ),
      photoPaths: List<String>.from(_photoPaths),
      photoCount: _photoPaths.length,
    );
    if (!mounted) return;
    if (!ok) {
      setState(() {
        _saving = false;
        _error = 'Revisa los datos de la pieza antes de guardar.';
      });
      return;
    }
    AppNavigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    final vehicles = widget.store.vehicles;
    final previewController = _cameraController;
    final latestPhoto = _photoPaths.isNotEmpty ? _photoPaths.last : null;

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
                            _vehicleMode
                                ? 'Ingreso de vehículo'
                                : 'Ingreso de pieza',
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: StitchText.titleMd.copyWith(
                              color: StitchColors.inverseOnSurface,
                            ),
                          ),
                          Text(
                            widget.store.isFirebaseConnected
                                ? 'Firebase conectado · Offline listo'
                                : 'Cola sin conexión lista',
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
                    onTap: _toggleFlash,
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
                    // Live Camera Viewfinder (CameraPreview or latest photo / fallback)
                    GestureDetector(
                      onTap: _takePhoto,
                      child: Container(
                        height: 230,
                        width: double.infinity,
                        decoration: const BoxDecoration(
                          color: StitchColors.inverseSurface,
                        ),
                        clipBehavior: Clip.hardEdge,
                        child: Stack(
                          fit: StackFit.expand,
                          children: [
                            if (_cameraReady &&
                                previewController != null &&
                                previewController.value.isInitialized)
                              FittedBox(
                                fit: BoxFit.cover,
                                child: SizedBox(
                                  width: previewController.value.previewSize?.height ?? 360,
                                  height: previewController.value.previewSize?.width ?? 480,
                                  child: CameraPreview(previewController),
                                ),
                              )
                            else if (latestPhoto != null)
                              PartPhotoWidget(
                                photoPath: latestPhoto,
                                fallbackReference: 'Foto ${_photoPaths.length}',
                                showReferenceLabel: false,
                              )
                            else
                              Center(
                                child: Padding(
                                  padding: const EdgeInsets.symmetric(horizontal: 24),
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
                                        _cameraStatusNote ??
                                            'Toca para capturar foto (${_photoPaths.length} / 4)',
                                        textAlign: TextAlign.center,
                                        style: StitchText.labelLg.copyWith(
                                          color: StitchColors.inverseOnSurface,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),

                            const Padding(
                              padding: EdgeInsets.all(20),
                              child: _ViewfinderCorners(),
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
                                    color: StitchColors.inverseSurface
                                        .withValues(alpha: 0.9),
                                    borderRadius:
                                        BorderRadius.circular(StitchRadius.full),
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
                              ColoredBox(
                                color: StitchColors.surfaceContainerLowest
                                    .withValues(alpha: 0.8),
                              ),
                          ],
                        ),
                      ),
                    ),
                    Container(height: 2, color: StitchColors.outline),

                    // Lower Light Panel: Thumbnails + Mode + Quick Tags + Form + Actions
                    Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          // Thumbnail Strip
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Expanded(
                                child: SingleChildScrollView(
                                  scrollDirection: Axis.horizontal,
                                  child: Row(
                                    children: [
                                      for (var i = 0; i < _photoPaths.length; i++) ...[
                                        GestureDetector(
                                          onTap: () => _removePhotoAt(i),
                                          child: Stack(
                                            children: [
                                              Container(
                                                width: 56,
                                                height: 56,
                                                clipBehavior: Clip.hardEdge,
                                                decoration: BoxDecoration(
                                                  color: StitchColors.surfaceContainer,
                                                  borderRadius: BorderRadius.circular(
                                                    StitchRadius.sm,
                                                  ),
                                                  border: Border.all(
                                                    color: i == _photoPaths.length - 1
                                                        ? StitchColors.primary
                                                        : StitchColors.outlineVariant,
                                                    width: 2,
                                                  ),
                                                ),
                                                child: PartPhotoWidget(
                                                  photoPath: _photoPaths[i],
                                                  fallbackReference: 'F${i + 1}',
                                                  iconSize: 18,
                                                ),
                                              ),
                                              Positioned(
                                                top: 2,
                                                right: 2,
                                                child: Container(
                                                  width: 18,
                                                  height: 18,
                                                  alignment: Alignment.center,
                                                  decoration: const BoxDecoration(
                                                    color: StitchColors.inverseSurface,
                                                    shape: BoxShape.circle,
                                                  ),
                                                  child: const Icon(
                                                    StitchIcons.close,
                                                    size: 12,
                                                    color: StitchColors.inverseOnSurface,
                                                  ),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                        const SizedBox(width: 8),
                                      ],
                                      if (_photoPaths.length < 4)
                                        GestureDetector(
                                          onTap: _takePhoto,
                                          child: Container(
                                            width: 56,
                                            height: 56,
                                            alignment: Alignment.center,
                                            decoration: BoxDecoration(
                                              color: StitchColors.surfaceContainerLow,
                                              borderRadius: BorderRadius.circular(
                                                StitchRadius.sm,
                                              ),
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
                                ),
                              ),
                              const SizedBox(width: 8),
                              Text(
                                '${_photoPaths.length} / 4 fotos',
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
                                    onTap: () =>
                                        setState(() => _selectedVehicleId = v.id),
                                  ),
                                for (final cat in const [
                                  'Motor',
                                  'Eléctrico',
                                  'Carrocería',
                                ])
                                  YardFilterChip(
                                    label: cat,
                                    selected: _selectedCategory == cat,
                                    minHeight: 40,
                                    onTap: () =>
                                        setState(() => _selectedCategory = cat),
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
                                borderRadius:
                                    BorderRadius.circular(StitchRadius.card),
                                border:
                                    Border.all(color: StitchColors.error, width: 2),
                              ),
                              child: Text(
                                _error!,
                                style: StitchText.bodyMd.copyWith(
                                  color: StitchColors.onErrorContainer,
                                ),
                              ),
                            ),
                          ],
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),

            // Pinned Shutter + Save Actions (2-column Stitch bar)
            Container(
              padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
              decoration: const BoxDecoration(
                color: StitchColors.surface,
                border: Border(
                  top: BorderSide(color: StitchColors.outlineVariant, width: 2),
                ),
              ),
              child: Row(
                children: [
                  Expanded(
                    child: CustomButton(
                      text: 'Tomar foto',
                      icon: StitchIcons.photoCamera,
                      minHeight: 56,
                      enabled: !_saving,
                      onTap: _takePhoto,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: CustomButton(
                      text: _saving
                          ? 'Guardando...'
                          : 'Guardar (${_photoPaths.length})',
                      icon: StitchIcons.cloudUpload,
                      isSecondary: true,
                      minHeight: 56,
                      enabled: !_saving,
                      onTap: _save,
                    ),
                  ),
                ],
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
        Positioned(top: 0, left: 0, child: Container(width: len, height: stroke, color: color)),
        Positioned(top: 0, left: 0, child: Container(width: stroke, height: len, color: color)),
        Positioned(top: 0, right: 0, child: Container(width: len, height: stroke, color: color)),
        Positioned(top: 0, right: 0, child: Container(width: stroke, height: len, color: color)),
        Positioned(bottom: 0, left: 0, child: Container(width: len, height: stroke, color: color)),
        Positioned(bottom: 0, left: 0, child: Container(width: stroke, height: len, color: color)),
        Positioned(bottom: 0, right: 0, child: Container(width: len, height: stroke, color: color)),
        Positioned(bottom: 0, right: 0, child: Container(width: stroke, height: len, color: color)),
      ],
    );
  }
}
