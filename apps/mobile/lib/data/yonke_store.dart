import 'dart:math';
import 'package:flutter/foundation.dart';

enum StaffRole { owner, staff }

enum PartStatus { available, inReview, sold }

enum PaymentMethod { cash, transfer, card }

class StaffMember {
  const StaffMember({
    required this.id,
    required this.name,
    required this.role,
    required this.roleSummary,
    required this.pin,
  });

  final String id;
  final String name;
  final StaffRole role;
  final String roleSummary;
  final String pin;
}

class YardVehicle {
  const YardVehicle({
    required this.id,
    required this.reference,
    required this.year,
    required this.make,
    required this.model,
    this.color = '',
    this.location = '',
  });

  final String id;
  final String reference;
  final int year;
  final String make;
  final String model;
  final String color;
  final String location;

  String get displayName => '$make $model $year';
}

class YardPart {
  const YardPart({
    required this.id,
    required this.reference,
    required this.vehicleId,
    required this.name,
    required this.category,
    required this.subtitle,
    required this.location,
    required this.priceMxn,
    this.status = PartStatus.available,
    this.photoCount = 1,
    this.soldBy,
    this.paymentMethod,
  });

  final String id;
  final String reference;
  final String vehicleId;
  final String name;
  final String category;
  final String subtitle;
  final String location;
  final int? priceMxn;
  final PartStatus status;
  final int photoCount;
  final String? soldBy;
  final PaymentMethod? paymentMethod;

  YardPart copyWith({
    PartStatus? status,
    int? photoCount,
    String? soldBy,
    PaymentMethod? paymentMethod,
  }) {
    return YardPart(
      id: id,
      reference: reference,
      vehicleId: vehicleId,
      name: name,
      category: category,
      subtitle: subtitle,
      location: location,
      priceMxn: priceMxn,
      status: status ?? this.status,
      photoCount: photoCount ?? this.photoCount,
      soldBy: soldBy ?? this.soldBy,
      paymentMethod: paymentMethod ?? this.paymentMethod,
    );
  }
}

class YonkeStore extends ChangeNotifier {
  YonkeStore() {
    _staff = [
      const StaffMember(
        id: 'staff-ana',
        name: 'Ana',
        role: StaffRole.staff,
        roleSummary: 'Mostrador y ventas · PIN ****',
        pin: '1234',
      ),
      const StaffMember(
        id: 'owner-david',
        name: 'David',
        role: StaffRole.owner,
        roleSummary: 'Dueño · Recuperación activa',
        pin: '2468',
      ),
      const StaffMember(
        id: 'staff-carlos',
        name: 'Carlos',
        role: StaffRole.staff,
        roleSummary: 'Patio e ingreso · PIN ****',
        pin: '5678',
      ),
    ];

    _vehicles = [
      const YardVehicle(
        id: 'v-1001',
        reference: 'V-1001',
        year: 2014,
        make: 'Nissan',
        model: 'Tsuru',
        color: 'Blanco',
        location: 'Pasillo B-04',
      ),
      const YardVehicle(
        id: 'v-1002',
        reference: 'V-1002',
        year: 2017,
        make: 'Chevrolet',
        model: 'Aveo',
        color: 'Gris',
        location: 'Pasillo A-12',
      ),
      const YardVehicle(
        id: 'v-1003',
        reference: 'V-1003',
        year: 2012,
        make: 'Honda',
        model: 'Civic',
        color: 'Plata',
        location: 'Patio C-02',
      ),
    ];

    _parts = [
      const YardPart(
        id: 'p-408',
        reference: 'P-408',
        vehicleId: 'v-1001',
        name: 'Alternador Bosch 110A',
        category: 'Eléctrico',
        subtitle: 'Nissan Tsuru · 2014 · 4 cil.',
        location: 'Pasillo B-04',
        priceMxn: 1450,
        status: PartStatus.available,
        photoCount: 2,
      ),
      const YardPart(
        id: 'p-412',
        reference: 'P-412',
        vehicleId: 'v-1002',
        name: 'Faro delantero derecho',
        category: 'Carrocería',
        subtitle: 'Chevrolet Aveo · 2017 · Original',
        location: 'Pasillo A-12',
        priceMxn: 890,
        status: PartStatus.available,
        photoCount: 1,
      ),
      const YardPart(
        id: 'p-419',
        reference: 'P-419',
        vehicleId: 'v-1003',
        name: 'Puerta delantera izq.',
        category: 'Carrocería',
        subtitle: 'Honda Civic · 2012 · Plata',
        location: 'Patio C-02',
        priceMxn: 3200,
        status: PartStatus.inReview,
        photoCount: 1,
      ),
      const YardPart(
        id: 'p-425',
        reference: 'P-425',
        vehicleId: 'v-1001',
        name: 'Bomba de agua 1.6L',
        category: 'Motor',
        subtitle: 'Nissan Tsuru · 2014 · Probada',
        location: 'Pasillo B-06',
        priceMxn: 650,
        status: PartStatus.available,
        photoCount: 2,
      ),
    ];
  }

  late List<StaffMember> _staff;
  late List<YardVehicle> _vehicles;
  late List<YardPart> _parts;

  StaffMember? _currentUser;
  String _deviceName = 'Patio Norte · Teléfono #02';
  bool _deviceLinked = true;
  String _enrollmentCode = '842915';
  String? _statusNotice;

  List<StaffMember> get staff => List.unmodifiable(_staff);
  List<YardVehicle> get vehicles => List.unmodifiable(_vehicles);
  List<YardPart> get parts => List.unmodifiable(_parts);
  StaffMember? get currentUser => _currentUser;
  String get deviceName => _deviceName;
  bool get deviceLinked => _deviceLinked;
  String get enrollmentCode => _enrollmentCode;
  String? get statusNotice => _statusNotice;

  int get activePartsCount =>
      _parts.where((p) => p.status != PartStatus.sold).length;

  int get pendingReviewCount =>
      _parts.where((p) => p.status == PartStatus.inReview).length;

  List<YardPart> get soldParts =>
      _parts.where((p) => p.status == PartStatus.sold).toList();

  YardVehicle? vehicleById(String id) {
    for (final v in _vehicles) {
      if (v.id == id) return v;
    }
    return null;
  }

  YardPart? partById(String id) {
    for (final p in _parts) {
      if (p.id == id) return p;
    }
    return null;
  }

  StaffMember? signIn(String staffId, String pin) {
    for (final member in _staff) {
      if (member.id == staffId && member.pin == pin) {
        _currentUser = member;
        _statusNotice = null;
        notifyListeners();
        return member;
      }
    }
    return null;
  }

  void signOut() {
    _currentUser = null;
    notifyListeners();
  }

  bool enrollPhone(String code, String name) {
    final cleanCode = code.replaceAll(RegExp(r'\D'), '');
    if (cleanCode.length != 6 || name.trim().length < 2) {
      return false;
    }
    _deviceName = name.trim();
    _deviceLinked = true;
    _statusNotice = 'Teléfono enlazado: $_deviceName';
    notifyListeners();
    return true;
  }

  String regenerateEnrollmentCode() {
    final rng = Random();
    final a = 100 + rng.nextInt(900);
    final b = 100 + rng.nextInt(900);
    _enrollmentCode = '$a$b';
    notifyListeners();
    return _enrollmentCode;
  }

  bool addStaffMember(String name, String pin) {
    final trimmed = name.trim();
    if (trimmed.length < 2 || !RegExp(r'^\d{4}$').hasMatch(pin)) {
      return false;
    }
    _staff = [
      ..._staff,
      StaffMember(
        id: 'staff-${_staff.length + 1}',
        name: trimmed,
        role: StaffRole.staff,
        roleSummary: 'Patio y mostrador · PIN ****',
        pin: pin,
      ),
    ];
    notifyListeners();
    return true;
  }

  bool addVehicle({
    required int year,
    required String make,
    required String model,
    String color = '',
    String location = '',
  }) {
    if (year < 1900 || year > 2100 || make.trim().isEmpty || model.trim().isEmpty) {
      return false;
    }
    final nextNum = 1001 + _vehicles.length;
    _vehicles = [
      YardVehicle(
        id: 'v-$nextNum',
        reference: 'V-$nextNum',
        year: year,
        make: make.trim(),
        model: model.trim(),
        color: color.trim(),
        location: location.trim().isEmpty ? 'Patio general' : location.trim(),
      ),
      ..._vehicles,
    ];
    _statusNotice = 'Vehículo V-$nextNum guardado';
    notifyListeners();
    return true;
  }

  bool addPart({
    required String vehicleId,
    required String name,
    required String category,
    required String location,
    int? priceMxn,
    int photoCount = 1,
  }) {
    final vehicle = vehicleById(vehicleId);
    if (vehicle == null || name.trim().isEmpty) return false;
    final nextNum = 430 + _parts.length * 4;
    _parts = [
      YardPart(
        id: 'p-$nextNum',
        reference: 'P-$nextNum',
        vehicleId: vehicle.id,
        name: name.trim(),
        category: category.trim().isEmpty ? 'Motor' : category.trim(),
        subtitle: '${vehicle.make} ${vehicle.model} · ${vehicle.year}',
        location: location.trim().isEmpty ? vehicle.location : location.trim(),
        priceMxn: priceMxn,
        status: PartStatus.available,
        photoCount: photoCount,
      ),
      ..._parts,
    ];
    _statusNotice = 'Pieza P-$nextNum guardada';
    notifyListeners();
    return true;
  }

  void incrementPartPhoto(String partId) {
    _parts = _parts
        .map((p) => p.id == partId ? p.copyWith(photoCount: p.photoCount + 1) : p)
        .toList();
    notifyListeners();
  }

  bool sellPart(String partId, PaymentMethod paymentMethod) {
    final target = partById(partId);
    if (target == null || target.status == PartStatus.sold) return false;
    _parts = _parts
        .map(
          (p) => p.id == partId
              ? p.copyWith(
                  status: PartStatus.sold,
                  soldBy: _currentUser?.name ?? 'Personal',
                  paymentMethod: paymentMethod,
                )
              : p,
        )
        .toList();
    _statusNotice = 'Venta registrada: ${target.name}';
    notifyListeners();
    return true;
  }

  bool restorePart(String partId) {
    if (_currentUser?.role != StaffRole.owner) return false;
    final target = partById(partId);
    if (target == null || target.status != PartStatus.sold) return false;
    _parts = _parts
        .map(
          (p) => p.id == partId
              ? p.copyWith(status: PartStatus.available)
              : p,
        )
        .toList();
    _statusNotice = 'Pieza restaurada al inventario';
    notifyListeners();
    return true;
  }
}

String formatMxn(int? priceMxn) {
  if (priceMxn == null) return 'Preguntar';
  final digits = priceMxn.toString();
  final buffer = StringBuffer('\$');
  for (var i = 0; i < digits.length; i++) {
    final remaining = digits.length - i;
    if (i > 0 && remaining % 3 == 0) {
      buffer.write(',');
    }
    buffer.write(digits[i]);
  }
  return buffer.toString();
}

String formatSixDigitCode(String code) {
  final clean = code.replaceAll(RegExp(r'\D'), '');
  if (clean.length == 6) {
    return '${clean.substring(0, 3)} ${clean.substring(3)}';
  }
  return code;
}
