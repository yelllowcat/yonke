import 'dart:async';
import 'dart:math';
import 'package:flutter/foundation.dart';
import 'firebase_repository.dart';

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
    this.photoPaths = const [],
  });

  final String id;
  final String reference;
  final int year;
  final String make;
  final String model;
  final String color;
  final String location;
  final List<String> photoPaths;

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
    this.photoPaths = const [],
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
  final List<String> photoPaths;
  final int photoCount;
  final String? soldBy;
  final PaymentMethod? paymentMethod;

  String? get primaryPhoto => photoPaths.isNotEmpty ? photoPaths.first : null;

  YardPart copyWith({
    PartStatus? status,
    List<String>? photoPaths,
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
      photoPaths: photoPaths ?? this.photoPaths,
      photoCount: photoCount ?? this.photoCount,
      soldBy: soldBy ?? this.soldBy,
      paymentMethod: paymentMethod ?? this.paymentMethod,
    );
  }
}

class YonkeStore extends ChangeNotifier {
  YonkeStore({FirebaseYonkeRepository? firebaseRepository})
      : _firebase = firebaseRepository {
    _staff = List<StaffMember>.from(_defaultStaff);
    _vehicles = List<YardVehicle>.from(_defaultVehicles);
    _parts = List<YardPart>.from(_defaultParts);

    if (_firebase != null) {
      _connectFirebase(_firebase);
    }
  }

  static const _defaultStaff = [
    StaffMember(
      id: 'staff-ana',
      name: 'Ana',
      role: StaffRole.staff,
      roleSummary: 'Mostrador y ventas · PIN ****',
      pin: '1234',
    ),
    StaffMember(
      id: 'owner-david',
      name: 'David',
      role: StaffRole.owner,
      roleSummary: 'Dueño · Recuperación activa',
      pin: '2468',
    ),
    StaffMember(
      id: 'staff-carlos',
      name: 'Carlos',
      role: StaffRole.staff,
      roleSummary: 'Patio e ingreso · PIN ****',
      pin: '5678',
    ),
  ];

  static const _defaultVehicles = [
    YardVehicle(
      id: 'v-1001',
      reference: 'V-1001',
      year: 2014,
      make: 'Nissan',
      model: 'Tsuru',
      color: 'Blanco',
      location: 'Pasillo B-04',
    ),
    YardVehicle(
      id: 'v-1002',
      reference: 'V-1002',
      year: 2017,
      make: 'Chevrolet',
      model: 'Aveo',
      color: 'Gris',
      location: 'Pasillo A-12',
    ),
    YardVehicle(
      id: 'v-1003',
      reference: 'V-1003',
      year: 2012,
      make: 'Honda',
      model: 'Civic',
      color: 'Plata',
      location: 'Patio C-02',
    ),
  ];

  static const _defaultParts = [
    YardPart(
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
    YardPart(
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
    YardPart(
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
    YardPart(
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

  final FirebaseYonkeRepository? _firebase;
  final List<StreamSubscription<dynamic>> _subscriptions = [];

  late List<StaffMember> _staff;
  late List<YardVehicle> _vehicles;
  late List<YardPart> _parts;

  StaffMember? _currentUser;
  String _deviceName = 'Patio Norte · Teléfono #02';
  bool _deviceLinked = true;
  bool _firebaseConnected = false;
  bool _isSyncing = false;
  String _enrollmentCode = '842915';
  String? _statusNotice;

  List<StaffMember> get staff => List.unmodifiable(_staff);
  List<YardVehicle> get vehicles => List.unmodifiable(_vehicles);
  List<YardPart> get parts => List.unmodifiable(_parts);
  StaffMember? get currentUser => _currentUser;
  String get deviceName => _deviceName;
  bool get deviceLinked => _deviceLinked;
  bool get isFirebaseConnected => _firebaseConnected;
  bool get isSyncing => _isSyncing;
  String get syncBadgeLabel =>
      _isSyncing ? 'Guardando...' : (_firebaseConnected ? 'Firebase' : 'Al día');
  String get enrollmentCode => _enrollmentCode;
  String? get statusNotice => _statusNotice;

  int get activePartsCount =>
      _parts.where((p) => p.status != PartStatus.sold).length;

  int get pendingReviewCount =>
      _parts.where((p) => p.status == PartStatus.inReview).length;

  List<YardPart> get soldParts =>
      _parts.where((p) => p.status == PartStatus.sold).toList();

  Future<void> _connectFirebase(FirebaseYonkeRepository repo) async {
    try {
      await repo.seedIfEmpty(
        defaultStaff: _defaultStaff,
        defaultVehicles: _defaultVehicles,
        defaultParts: _defaultParts,
      );
      _firebaseConnected = true;
      _subscriptions.add(
        repo.watchStaff().listen((list) {
          if (list.isNotEmpty) {
            _staff = list;
            notifyListeners();
          }
        }),
      );
      _subscriptions.add(
        repo.watchVehicles().listen((list) {
          _vehicles = list;
          notifyListeners();
        }),
      );
      _subscriptions.add(
        repo.watchParts().listen((list) {
          _parts = list;
          notifyListeners();
        }),
      );
      notifyListeners();
    } catch (_) {
      // Fall back gracefully to local offline data if Firebase project is unreachable.
      _firebaseConnected = false;
      notifyListeners();
    }
  }

  @override
  void dispose() {
    for (final sub in _subscriptions) {
      sub.cancel();
    }
    super.dispose();
  }

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

  Future<bool> enrollPhone(String code, String name) async {
    final cleanCode = code.replaceAll(RegExp(r'\D'), '');
    if (cleanCode.length != 6 || name.trim().length < 2) {
      return false;
    }
    _deviceName = name.trim();
    _deviceLinked = true;
    _statusNotice = 'Teléfono enlazado: $_deviceName';
    notifyListeners();

    if (_firebase != null && _firebaseConnected) {
      unawaited(
        _firebase
            .registerDevice(code: cleanCode, deviceName: _deviceName)
            .catchError((_) {}),
      );
    }
    return true;
  }

  String regenerateEnrollmentCode() {
    final rng = Random();
    final a = 100 + rng.nextInt(900);
    final b = 100 + rng.nextInt(900);
    _enrollmentCode = '$a$b';
    notifyListeners();

    if (_firebase != null && _firebaseConnected) {
      unawaited(
        _firebase.saveEnrollmentCode(_enrollmentCode).catchError((_) {}),
      );
    }
    return _enrollmentCode;
  }

  Future<bool> addStaffMember(String name, String pin) async {
    final trimmed = name.trim();
    if (trimmed.length < 2 || !RegExp(r'^\d{4}$').hasMatch(pin)) {
      return false;
    }
    final member = StaffMember(
      id: 'staff-${DateTime.now().millisecondsSinceEpoch}',
      name: trimmed,
      role: StaffRole.staff,
      roleSummary: 'Patio y mostrador · PIN ****',
      pin: pin,
    );
    _staff = [..._staff, member];
    notifyListeners();

    if (_firebase != null && _firebaseConnected) {
      await _runCloudWrite(() => _firebase.addStaffMember(member));
    }
    return true;
  }

  Future<bool> addVehicle({
    required int year,
    required String make,
    required String model,
    String color = '',
    String location = '',
    List<String> photoPaths = const [],
  }) async {
    if (year < 1900 ||
        year > 2100 ||
        make.trim().isEmpty ||
        model.trim().isEmpty) {
      return false;
    }
    final nextNum = 1001 + _vehicles.length;
    final vehicle = YardVehicle(
      id: 'v-${DateTime.now().millisecondsSinceEpoch}',
      reference: 'V-$nextNum',
      year: year,
      make: make.trim(),
      model: model.trim(),
      color: color.trim(),
      location: location.trim().isEmpty ? 'Patio general' : location.trim(),
      photoPaths: List<String>.from(photoPaths),
    );
    _vehicles = [vehicle, ..._vehicles];
    _statusNotice = 'Vehículo ${vehicle.reference} guardado';
    notifyListeners();

    if (_firebase != null && _firebaseConnected) {
      await _runCloudWrite(() => _firebase.saveVehicle(vehicle));
    }
    return true;
  }

  Future<bool> addPart({
    required String vehicleId,
    required String name,
    required String category,
    required String location,
    int? priceMxn,
    List<String> photoPaths = const [],
    int? photoCount,
  }) async {
    final vehicle = vehicleById(vehicleId);
    if (vehicle == null || name.trim().isEmpty) return false;
    final nextNum = 430 + _parts.length * 4;
    final resolvedCount = photoCount ?? (photoPaths.isNotEmpty ? photoPaths.length : 1);
    final part = YardPart(
      id: 'p-${DateTime.now().millisecondsSinceEpoch}',
      reference: 'P-$nextNum',
      vehicleId: vehicle.id,
      name: name.trim(),
      category: category.trim().isEmpty ? 'Motor' : category.trim(),
      subtitle: '${vehicle.make} ${vehicle.model} · ${vehicle.year}',
      location: location.trim().isEmpty ? vehicle.location : location.trim(),
      priceMxn: priceMxn,
      status: PartStatus.available,
      photoPaths: List<String>.from(photoPaths),
      photoCount: resolvedCount,
    );
    _parts = [part, ..._parts];
    _statusNotice = 'Pieza ${part.reference} guardada';
    notifyListeners();

    if (_firebase != null && _firebaseConnected) {
      await _runCloudWrite(() => _firebase.savePart(part));
    }
    return true;
  }

  Future<void> attachPartPhoto(String partId, String localPath) async {
    _parts = _parts
        .map(
          (p) => p.id == partId
              ? p.copyWith(
                  photoPaths: [...p.photoPaths, localPath],
                  photoCount: p.photoCount + 1,
                )
              : p,
        )
        .toList();
    notifyListeners();

    if (_firebase != null && _firebaseConnected) {
      await _runCloudWrite(() => _firebase.attachPartPhoto(partId, localPath));
    }
  }

  Future<bool> sellPart(String partId, PaymentMethod paymentMethod) async {
    final target = partById(partId);
    if (target == null || target.status == PartStatus.sold) return false;
    final seller = _currentUser?.name ?? 'Personal';

    _parts = _parts
        .map(
          (p) => p.id == partId
              ? p.copyWith(
                  status: PartStatus.sold,
                  soldBy: seller,
                  paymentMethod: paymentMethod,
                )
              : p,
        )
        .toList();
    _statusNotice = 'Venta registrada: ${target.name}';
    notifyListeners();

    if (_firebase != null && _firebaseConnected) {
      await _runCloudWrite(
        () => _firebase.sellPartAtomic(
          partId: partId,
          soldBy: seller,
          paymentMethod: paymentMethod,
        ),
      );
    }
    return true;
  }

  Future<bool> restorePart(String partId) async {
    if (_currentUser?.role != StaffRole.owner) return false;
    final target = partById(partId);
    if (target == null || target.status != PartStatus.sold) return false;
    _parts = _parts
        .map(
          (p) => p.id == partId ? p.copyWith(status: PartStatus.available) : p,
        )
        .toList();
    _statusNotice = 'Pieza restaurada al inventario';
    notifyListeners();

    if (_firebase != null && _firebaseConnected) {
      await _runCloudWrite(() => _firebase.restorePart(partId));
    }
    return true;
  }

  Future<void> _runCloudWrite(Future<dynamic> Function() action) async {
    _isSyncing = true;
    notifyListeners();
    try {
      await action();
    } catch (_) {
      // Firestore offline cache queues mutations when network returns.
    } finally {
      _isSyncing = false;
      notifyListeners();
    }
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
