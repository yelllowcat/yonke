import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:yonke_mobile/main.dart';

void main() {
  Future<void> enterPin(WidgetTester tester, String pin) async {
    for (final digit in pin.split('')) {
      final finder = find.text(digit).last;
      await tester.ensureVisible(finder);
      await tester.tap(finder);
      await tester.pump();
    }
  }

  testWidgets('Login pushes EnrollPhoneScreen and pops back to Login', (tester) async {
    tester.view.physicalSize = const Size(430, 932);
    tester.view.devicePixelRatio = 1.0;
    addTearDown(tester.view.reset);

    await tester.pumpWidget(YonkeFacilApp());
    expect(find.text('¿Quién entra al turno?'), findsOneWidget);

    final enrollBtn = find.text('Vincular teléfono con código');
    await tester.ensureVisible(enrollBtn);
    await tester.tap(enrollBtn);
    await tester.pumpAndSettle();

    expect(find.text('Vincular teléfono'), findsOneWidget);

    final backBtn = find.text('Regresar al acceso por PIN');
    await tester.ensureVisible(backBtn);
    await tester.tap(backBtn);
    await tester.pumpAndSettle();

    expect(find.text('¿Quién entra al turno?'), findsOneWidget);
  });

  testWidgets('Full Stitch + Navigator stack flow: PIN -> Inventory -> Detail Sale -> Camera -> Team -> PopUntil Login', (tester) async {
    tester.view.physicalSize = const Size(430, 932);
    tester.view.devicePixelRatio = 1.0;
    addTearDown(tester.view.reset);

    await tester.pumpWidget(YonkeFacilApp());

    // Select Owner David and sign in with PIN 2468
    await tester.tap(find.text('David (Dueño)'));
    await tester.pump();
    await enterPin(tester, '2468');

    final enterBtn = find.text('Entrar al patio');
    await tester.ensureVisible(enterBtn);
    await tester.tap(enterBtn);
    await tester.pumpAndSettle();

    // Screen 2: Inventario y Patio
    expect(find.text('PIEZAS ACTIVAS'), findsOneWidget);
    expect(find.text('Alternador Bosch 110A'), findsOneWidget);

    // Push Screen 4: Detalle y Venta Rápida
    await tester.tap(find.text('Alternador Bosch 110A'));
    await tester.pumpAndSettle();

    expect(find.text('Pieza #P-408'), findsOneWidget);
    expect(find.text('Total a cobrar'), findsOneWidget);

    // Select payment method and confirm atomic sale -> pops back to Inventory
    await tester.tap(find.text('Transferencia'));
    await tester.pump();
    await tester.tap(find.text('Confirmar venta · \$1,450'));
    await tester.pumpAndSettle();

    expect(find.text('PIEZAS ACTIVAS'), findsOneWidget);

    // Push Screen 3: Cámara e Ingreso from bottom center camera button
    await tester.tap(find.text('Cámara'));
    await tester.pumpAndSettle();
    expect(find.text('Cola sin conexión lista'), findsOneWidget);
    expect(find.text('Tomar foto'), findsOneWidget);

    // Pop back to Inventory
    await tester.tap(find.bySemanticsLabel('Regresar al inventario'));
    await tester.pumpAndSettle();
    expect(find.text('PIEZAS ACTIVAS'), findsOneWidget);

    // Switch to Screen 5: Equipo y Dueño
    await tester.tap(find.text('Equipo'));
    await tester.pumpAndSettle();
    expect(find.text('Enlazar teléfono nuevo'), findsOneWidget);
    expect(find.text('Personal con PIN (3)'), findsOneWidget);

    // Sign out -> Navigator.popUntil(isFirst) returns to LoginScreen
    final logoutBtn = find.text('Cerrar sesión');
    await tester.ensureVisible(logoutBtn);
    await tester.tap(logoutBtn);
    await tester.pumpAndSettle();

    expect(find.text('¿Quién entra al turno?'), findsOneWidget);
  });
}
