import 'package:flutter/widgets.dart';
import '../components/custom_button.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';
import 'enroll_phone_screen.dart';
import 'inventory_screen.dart';

/// Screen 1: Acceso por PIN (Stitch 01_acceso_pin.html)
/// Uses `Navigator.push` with `PageRouteBuilder` exactly like `eventhub-flutter`.
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key, required this.store});

  final YonkeStore store;

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  late String _selectedStaffId;
  String _pin = '';
  bool _pinError = false;

  @override
  void initState() {
    super.initState();
    _selectedStaffId = widget.store.staff.first.id;
  }

  void _tapDigit(String digit) {
    if (_pin.length >= 4) return;
    setState(() {
      _pin += digit;
      _pinError = false;
    });
  }

  void _backspace() {
    if (_pin.isEmpty) return;
    setState(() {
      _pin = _pin.substring(0, _pin.length - 1);
      _pinError = false;
    });
  }

  void _clear() {
    setState(() {
      _pin = '';
      _pinError = false;
    });
  }

  void _submitPin() {
    final user = widget.store.signIn(_selectedStaffId, _pin);
    if (user == null) {
      setState(() {
        _pinError = true;
        _pin = '';
      });
      return;
    }
    setState(() {
      _pin = '';
      _pinError = false;
    });
    AppNavigator.push(context, InventoryScreen(store: widget.store));
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: widget.store,
      builder: (context, _) {
        final staffList = widget.store.staff;
        final selected = staffList.firstWhere(
          (m) => m.id == _selectedStaffId,
          orElse: () => staffList.first,
        );

        return ColoredBox(
          color: StitchColors.surface,
          child: SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // Brand & Device Status Header
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Expanded(
                        child: Row(
                          children: [
                            Container(
                              width: 40,
                              height: 40,
                              alignment: Alignment.center,
                              decoration: BoxDecoration(
                                color: StitchColors.primary,
                                borderRadius: BorderRadius.circular(StitchRadius.md),
                                border: Border.all(
                                  color: StitchColors.primary,
                                  width: 2,
                                ),
                              ),
                              child: const Icon(
                                StitchIcons.carRepair,
                                size: 24,
                                color: StitchColors.onPrimary,
                              ),
                            ),
                            const SizedBox(width: 10),
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text('Yonke Fácil', style: StitchText.headlineMd),
                                  Text(
                                    widget.store.deviceName,
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                    style: StitchText.labelSm,
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 8),
                      StatusBadge(
                        text: widget.store.deviceLinked ? 'Enlazado' : 'Sin enlazar',
                        backgroundColor: StitchColors.successContainer,
                        textColor: StitchColors.onSuccessContainer,
                        borderColor: StitchColors.success,
                        icon: StitchIcons.verifiedUser,
                      ),
                    ],
                  ),
                  const SizedBox(height: 24),

                  // Staff Selector Chips
                  const Text('¿Quién entra al turno?', style: StitchText.headlineLgMobile),
                  const SizedBox(height: 10),
                  Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: [
                      for (final member in staffList)
                        YardFilterChip(
                          label: member.role == StaffRole.owner
                              ? '${member.name} (Dueño)'
                              : member.name,
                          selected: selected.id == member.id,
                          minHeight: 48,
                          icon: member.role == StaffRole.owner
                              ? StitchIcons.adminPanelSettings
                              : StitchIcons.person,
                          onTap: () => setState(() {
                            _selectedStaffId = member.id;
                            _pin = '';
                            _pinError = false;
                          }),
                        ),
                    ],
                  ),
                  const SizedBox(height: 20),

                  // PIN Dots Box
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: StitchColors.surfaceContainerLowest,
                      borderRadius: BorderRadius.circular(StitchRadius.card),
                      border: Border.all(
                        color: _pinError ? StitchColors.error : StitchColors.outline,
                        width: 2,
                      ),
                    ),
                    child: Column(
                      children: [
                        RichText(
                          textAlign: TextAlign.center,
                          text: TextSpan(
                            style: StitchText.bodyMd,
                            children: [
                              const TextSpan(text: 'Escribe el PIN de 4 dígitos para '),
                              TextSpan(
                                text: selected.name,
                                style: StitchText.bodyMd.copyWith(
                                  fontWeight: FontWeight.w700,
                                  color: StitchColors.onSurface,
                                ),
                              ),
                            ],
                          ),
                        ),
                        const SizedBox(height: 12),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: List.generate(4, (index) {
                            final filled = index < _pin.length;
                            return Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 8),
                              child: Container(
                                width: 20,
                                height: 20,
                                decoration: BoxDecoration(
                                  shape: BoxShape.circle,
                                  color: filled
                                      ? StitchColors.primary
                                      : StitchColors.surfaceContainer,
                                  border: Border.all(
                                    color: filled
                                        ? StitchColors.primary
                                        : StitchColors.outline,
                                    width: 2,
                                  ),
                                ),
                              ),
                            );
                          }),
                        ),
                        if (_pinError) ...[
                          const SizedBox(height: 10),
                          Text(
                            'PIN incorrecto. Intenta otra vez.',
                            style: StitchText.bodyMd.copyWith(
                              color: StitchColors.error,
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                        ],
                      ],
                    ),
                  ),
                  const SizedBox(height: 20),

                  // Tactile 3x4 Numeric Keypad
                  for (final row in const [
                    ['1', '2', '3'],
                    ['4', '5', '6'],
                    ['7', '8', '9'],
                  ]) ...[
                    Row(
                      children: [
                        for (var i = 0; i < row.length; i++) ...[
                          if (i > 0) const SizedBox(width: 12),
                          Expanded(
                            child: _KeypadButton(
                              label: row[i],
                              onTap: () => _tapDigit(row[i]),
                            ),
                          ),
                        ],
                      ],
                    ),
                    const SizedBox(height: 12),
                  ],
                  Row(
                    children: [
                      Expanded(
                        child: _KeypadActionButton(
                          label: 'Borrar',
                          onTap: _clear,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: _KeypadButton(
                          label: '0',
                          onTap: () => _tapDigit('0'),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: _KeypadActionButton(
                          icon: StitchIcons.backspace,
                          semanticLabel: 'Retroceder',
                          onTap: _backspace,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 24),

                  // Footer Actions (Navigator.push -> InventoryScreen / EnrollPhoneScreen)
                  CustomButton(
                    text: 'Entrar al patio',
                    icon: StitchIcons.login,
                    minHeight: 56,
                    enabled: _pin.length == 4,
                    onTap: _submitPin,
                  ),
                  const SizedBox(height: 12),
                  CustomButton(
                    text: 'Vincular teléfono con código',
                    icon: StitchIcons.phonelinkSetup,
                    isSecondary: true,
                    subtleBorder: true,
                    minHeight: 48,
                    onTap: () => AppNavigator.push(
                      context,
                      EnrollPhoneScreen(store: widget.store),
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}

class _KeypadButton extends StatelessWidget {
  const _KeypadButton({required this.label, required this.onTap});

  final String label;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: onTap,
      child: Container(
        height: 60,
        alignment: Alignment.center,
        decoration: BoxDecoration(
          color: StitchColors.surfaceContainerLowest,
          borderRadius: BorderRadius.circular(StitchRadius.md),
          border: Border.all(color: StitchColors.outline, width: 2),
        ),
        child: Text(label, style: StitchText.monoLg),
      ),
    );
  }
}

class _KeypadActionButton extends StatelessWidget {
  const _KeypadActionButton({
    this.label,
    this.icon,
    this.semanticLabel,
    required this.onTap,
  });

  final String? label;
  final IconData? icon;
  final String? semanticLabel;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Semantics(
      button: true,
      label: semanticLabel ?? label,
      child: GestureDetector(
        behavior: HitTestBehavior.opaque,
        onTap: onTap,
        child: Container(
          height: 60,
          alignment: Alignment.center,
          decoration: BoxDecoration(
            color: StitchColors.surfaceContainerLow,
            borderRadius: BorderRadius.circular(StitchRadius.md),
            border: Border.all(color: StitchColors.outlineVariant, width: 2),
          ),
          child: icon != null
              ? Icon(icon, size: 24, color: StitchColors.onSurface)
              : Text(
                  label ?? '',
                  style: StitchText.labelLg.copyWith(
                    color: StitchColors.onSurfaceVariant,
                  ),
                ),
        ),
      ),
    );
  }
}
