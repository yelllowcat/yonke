import 'package:flutter/widgets.dart';
import '../components/custom_button.dart';
import '../components/custom_input.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';

/// Phone enrollment screen pushed from LoginScreen and dismissed with `Navigator.pop(context)`,
/// mirroring the push/pop flow of `register_screen.dart` in `eventhub-flutter`.
class EnrollPhoneScreen extends StatefulWidget {
  const EnrollPhoneScreen({super.key, required this.store});

  final YonkeStore store;

  @override
  State<EnrollPhoneScreen> createState() => _EnrollPhoneScreenState();
}

class _EnrollPhoneScreenState extends State<EnrollPhoneScreen> {
  final _codeController = TextEditingController();
  final _nameController = TextEditingController();
  String? _error;

  @override
  void dispose() {
    _codeController.dispose();
    _nameController.dispose();
    super.dispose();
  }

  Future<void> _enroll() async {
    final ok = await widget.store.enrollPhone(
      _codeController.text,
      _nameController.text,
    );
    if (!mounted) return;
    if (!ok) {
      setState(() {
        _error = 'Escribe un código válido de 6 dígitos y un nombre para el teléfono.';
      });
      return;
    }
    AppNavigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return ColoredBox(
      color: StitchColors.surface,
      child: SafeArea(
        child: Column(
          children: [
            ContextualBackHeader(
              title: 'Vincular teléfono',
              subtitle: 'Código de 6 dígitos del dueño',
              onBack: () => AppNavigator.pop(context),
            ),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: StitchColors.surfaceContainerLowest,
                        borderRadius: BorderRadius.circular(StitchRadius.card),
                        border: Border.all(color: StitchColors.outline, width: 2),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          CustomInput(
                            controller: _codeController,
                            label: 'Código de 6 dígitos',
                            hint: '842915',
                            helperText: 'Pídelo al dueño en la pantalla Equipo.',
                            keyboardType: TextInputType.number,
                            prefixIcon: StitchIcons.phonelinkLock,
                            onChanged: (_) => setState(() => _error = null),
                          ),
                          const SizedBox(height: 16),
                          CustomInput(
                            controller: _nameController,
                            label: 'Nombre del teléfono',
                            hint: 'Ej. Patio Norte · Teléfono #03',
                            prefixIcon: StitchIcons.phonelinkSetup,
                            onChanged: (_) => setState(() => _error = null),
                          ),
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
                          const SizedBox(height: 20),
                          CustomButton(
                            text: 'Conectar teléfono',
                            icon: StitchIcons.verifiedUser,
                            onTap: _enroll,
                          ),
                          const SizedBox(height: 12),
                          CustomButton(
                            text: 'Regresar al acceso por PIN',
                            isSecondary: true,
                            subtleBorder: true,
                            onTap: () => AppNavigator.pop(context),
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
