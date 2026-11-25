import 'package:flutter/widgets.dart';
import '../components/custom_button.dart';
import '../components/custom_input.dart';
import '../components/yard_shell.dart';
import '../data/yonke_store.dart';
import '../navigation/app_navigator.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';

/// Screen 5: Equipo y Dueño (Stitch 05_equipo_dueno.html)
class TeamOwnerScreen extends StatefulWidget {
  const TeamOwnerScreen({super.key, required this.store});

  final YonkeStore store;

  @override
  State<TeamOwnerScreen> createState() => _TeamOwnerScreenState();
}

class _TeamOwnerScreenState extends State<TeamOwnerScreen> {
  bool _showAddStaff = false;
  final _nameController = TextEditingController();
  final _pinController = TextEditingController();
  String? _formError;

  @override
  void dispose() {
    _nameController.dispose();
    _pinController.dispose();
    super.dispose();
  }

  Future<void> _addStaff() async {
    final ok = await widget.store.addStaffMember(
      _nameController.text,
      _pinController.text.trim(),
    );
    if (!mounted) return;
    if (!ok) {
      setState(() => _formError = 'Escribe un nombre y un PIN de 4 números.');
      return;
    }
    setState(() {
      _nameController.clear();
      _pinController.clear();
      _formError = null;
      _showAddStaff = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: widget.store,
      builder: (context, _) {
        final isOwner = widget.store.currentUser?.role == StaffRole.owner;
        final staff = widget.store.staff;

        return YardScaffold(
          store: widget.store,
          title: 'Equipo y Dueño',
          currentTab: YardTab.team,
          body: ListView(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 28),
            children: [
              // 6-Digit Phone Enrollment Code Card (Owner Only)
              if (isOwner) ...[
                Container(
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: StitchColors.primaryContainer,
                    borderRadius: BorderRadius.circular(StitchRadius.card),
                    border: Border.all(color: StitchColors.primary, width: 2),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'Enlazar teléfono nuevo',
                                  style: StitchText.titleLg.copyWith(
                                    color: StitchColors.onPrimaryContainer,
                                  ),
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  'Ingresa este código de 6 dígitos en el celular del empleado.',
                                  style: StitchText.bodyMd.copyWith(
                                    color: StitchColors.onPrimaryContainer,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(width: 8),
                          const Icon(
                            StitchIcons.phonelinkLock,
                            size: 28,
                            color: StitchColors.primary,
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 12,
                        ),
                        decoration: BoxDecoration(
                          color: StitchColors.surfaceContainerLowest,
                          borderRadius: BorderRadius.circular(StitchRadius.md),
                          border: Border.all(
                            color: StitchColors.primary,
                            width: 2,
                          ),
                        ),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Expanded(
                              child: Text(
                                formatSixDigitCode(widget.store.enrollmentCode),
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                                style: StitchText.monoDisplay.copyWith(
                                  letterSpacing: 3,
                                ),
                              ),
                            ),
                            const SizedBox(width: 8),
                            const StatusBadge(
                              text: 'Expira 10m',
                              backgroundColor: StitchColors.warningContainer,
                              textColor: StitchColors.onWarningContainer,
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 12),
                      CustomButton(
                        text: 'Generar código nuevo',
                        icon: StitchIcons.refresh,
                        minHeight: 52,
                        onTap: () => widget.store.regenerateEnrollmentCode(),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 24),
              ],

              // Staff List Header + Add Button
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      'Personal con PIN (${staff.length})',
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: StitchText.titleLg,
                    ),
                  ),
                  const SizedBox(width: 8),
                  if (isOwner)
                    GestureDetector(
                      behavior: HitTestBehavior.opaque,
                      onTap: () => setState(() {
                        _showAddStaff = !_showAddStaff;
                        _formError = null;
                      }),
                      child: Container(
                        constraints: const BoxConstraints(minHeight: 44),
                        padding: const EdgeInsets.symmetric(
                          horizontal: 12,
                          vertical: 6,
                        ),
                        decoration: BoxDecoration(
                          color: StitchColors.surfaceContainerLowest,
                          borderRadius: BorderRadius.circular(StitchRadius.md),
                          border: Border.all(
                            color: StitchColors.secondary,
                            width: 2,
                          ),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const Icon(
                              StitchIcons.personAdd,
                              size: 18,
                              color: StitchColors.secondary,
                            ),
                            const SizedBox(width: 4),
                            Text(
                              'Agregar',
                              style: StitchText.labelSm.copyWith(
                                color: StitchColors.secondary,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                ],
              ),
              const SizedBox(height: 12),

              if (isOwner && _showAddStaff) ...[
                Container(
                  padding: const EdgeInsets.all(14),
                  decoration: BoxDecoration(
                    color: StitchColors.surfaceContainerLowest,
                    borderRadius: BorderRadius.circular(StitchRadius.card),
                    border: Border.all(color: StitchColors.secondary, width: 2),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      CustomInput(
                        controller: _nameController,
                        label: 'Nombre del empleado',
                        hint: 'Ej. María Torres',
                        helperText: 'Se mostrará en la pantalla de PIN',
                      ),
                      const SizedBox(height: 12),
                      CustomInput(
                        controller: _pinController,
                        label: 'PIN de 4 dígitos',
                        hint: '4321',
                        obscureText: true,
                        keyboardType: TextInputType.number,
                      ),
                      if (_formError != null) ...[
                        const SizedBox(height: 8),
                        Text(
                          _formError!,
                          style: StitchText.bodyMd.copyWith(
                            color: StitchColors.error,
                          ),
                        ),
                      ],
                      const SizedBox(height: 12),
                      CustomButton(
                        text: 'Guardar empleado',
                        icon: StitchIcons.personAdd,
                        onTap: _addStaff,
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 12),
              ],

              // Staff Cards
              for (var i = 0; i < staff.length; i++) ...[
                _StaffCard(member: staff[i], index: i),
                const SizedBox(height: 8),
              ],
              const SizedBox(height: 16),

              // Sign Out -> Navigator.popUntil(isFirst) like eventhub-flutter
              CustomButton(
                text: 'Cerrar sesión',
                icon: StitchIcons.logout,
                isSecondary: true,
                subtleBorder: true,
                onTap: () {
                  widget.store.signOut();
                  AppNavigator.popToLogin(context);
                },
              ),
            ],
          ),
        );
      },
    );
  }
}

class _StaffCard extends StatelessWidget {
  const _StaffCard({required this.member, required this.index});

  final StaffMember member;
  final int index;

  @override
  Widget build(BuildContext context) {
    final isOwner = member.role == StaffRole.owner;
    final avatarBg = isOwner
        ? StitchColors.primaryContainer
        : index.isEven
            ? StitchColors.secondaryContainer
            : StitchColors.surfaceContainer;
    final avatarFg = isOwner
        ? StitchColors.onPrimaryContainer
        : index.isEven
            ? StitchColors.onSecondaryContainer
            : StitchColors.onSurface;

    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: StitchColors.surfaceContainerLowest,
        borderRadius: BorderRadius.circular(StitchRadius.card),
        border: Border.all(color: StitchColors.outlineVariant, width: 2),
      ),
      child: Row(
        children: [
          Container(
            width: 44,
            height: 44,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: avatarBg,
              borderRadius: BorderRadius.circular(StitchRadius.md),
            ),
            child: Text(
              member.name.substring(0, 1).toUpperCase(),
              style: StitchText.headlineMd.copyWith(color: avatarFg),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(member.name, style: StitchText.titleMd),
                Text(
                  member.roleSummary,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: StitchText.bodyMd,
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          StatusBadge(
            text: isOwner ? 'Dueño' : 'Activo',
            backgroundColor: isOwner
                ? StitchColors.secondaryContainer
                : StitchColors.successContainer,
            textColor: isOwner
                ? StitchColors.onSecondaryContainer
                : StitchColors.onSuccessContainer,
          ),
        ],
      ),
    );
  }
}
