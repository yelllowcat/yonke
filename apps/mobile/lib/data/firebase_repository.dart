import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_storage/firebase_storage.dart';
import 'package:image_picker/image_picker.dart' show XFile;
import 'yonke_store.dart';

/// Cloud Firestore + Firebase Storage repository for Yonke Fácil.
/// Collections:
/// - `staff`
/// - `vehicles`
/// - `parts`
/// - `devices`
/// - `enrollment_codes`
class FirebaseYonkeRepository {
  FirebaseYonkeRepository({
    FirebaseFirestore? firestore,
    FirebaseStorage? storage,
  })  : _firestore = firestore ?? FirebaseFirestore.instance,
        _storage = storage ?? FirebaseStorage.instance {
    _firestore.settings = const Settings(
      persistenceEnabled: true,
      cacheSizeBytes: Settings.CACHE_SIZE_UNLIMITED,
    );
  }

  final FirebaseFirestore _firestore;
  final FirebaseStorage _storage;

  CollectionReference<Map<String, dynamic>> get _staffCol =>
      _firestore.collection('staff');
  CollectionReference<Map<String, dynamic>> get _vehiclesCol =>
      _firestore.collection('vehicles');
  CollectionReference<Map<String, dynamic>> get _partsCol =>
      _firestore.collection('parts');
  CollectionReference<Map<String, dynamic>> get _devicesCol =>
      _firestore.collection('devices');
  CollectionReference<Map<String, dynamic>> get _codesCol =>
      _firestore.collection('enrollment_codes');

  /// Seeds initial yard staff, vehicles, and parts only if the database is brand new.
  Future<void> seedIfEmpty({
    required List<StaffMember> defaultStaff,
    required List<YardVehicle> defaultVehicles,
    required List<YardPart> defaultParts,
  }) async {
    final existingStaff = await _staffCol.limit(1).get();
    if (existingStaff.docs.isNotEmpty) return;

    final batch = _firestore.batch();
    for (final s in defaultStaff) {
      batch.set(_staffCol.doc(s.id), {
        'name': s.name,
        'role': s.role.name,
        'roleSummary': s.roleSummary,
        'pin': s.pin,
        'createdAt': FieldValue.serverTimestamp(),
      });
    }
    for (final v in defaultVehicles) {
      batch.set(_vehiclesCol.doc(v.id), {
        'reference': v.reference,
        'year': v.year,
        'make': v.make,
        'model': v.model,
        'color': v.color,
        'location': v.location,
        'photoUrls': v.photoPaths,
        'createdAt': FieldValue.serverTimestamp(),
      });
    }
    for (final p in defaultParts) {
      batch.set(_partsCol.doc(p.id), {
        'reference': p.reference,
        'vehicleId': p.vehicleId,
        'name': p.name,
        'category': p.category,
        'subtitle': p.subtitle,
        'location': p.location,
        'priceMxn': p.priceMxn,
        'status': p.status.name,
        'photoUrls': p.photoPaths,
        'photoCount': p.photoCount,
        'soldBy': p.soldBy,
        'paymentMethod': p.paymentMethod?.name,
        'createdAt': FieldValue.serverTimestamp(),
        'updatedAt': FieldValue.serverTimestamp(),
      });
    }
    await batch.commit();
  }

  Stream<List<StaffMember>> watchStaff() {
    return _staffCol.orderBy('name').snapshots().map(
          (snap) => snap.docs.map((doc) {
            final d = doc.data();
            return StaffMember(
              id: doc.id,
              name: (d['name'] as String?) ?? 'Empleado',
              role: (d['role'] as String?) == StaffRole.owner.name
                  ? StaffRole.owner
                  : StaffRole.staff,
              roleSummary:
                  (d['roleSummary'] as String?) ?? 'Patio y mostrador · PIN ****',
              pin: (d['pin'] as String?) ?? '0000',
            );
          }).toList(),
        );
  }

  Stream<List<YardVehicle>> watchVehicles() {
    return _vehiclesCol
        .orderBy('createdAt', descending: true)
        .snapshots()
        .map(
          (snap) => snap.docs.map((doc) {
            final d = doc.data();
            final urls = (d['photoUrls'] as List<dynamic>?)
                    ?.whereType<String>()
                    .toList() ??
                const <String>[];
            return YardVehicle(
              id: doc.id,
              reference: (d['reference'] as String?) ?? doc.id.toUpperCase(),
              year: (d['year'] as num?)?.toInt() ?? 2015,
              make: (d['make'] as String?) ?? '',
              model: (d['model'] as String?) ?? '',
              color: (d['color'] as String?) ?? '',
              location: (d['location'] as String?) ?? '',
              photoPaths: urls,
            );
          }).toList(),
        );
  }

  Stream<List<YardPart>> watchParts() {
    return _partsCol
        .orderBy('createdAt', descending: true)
        .snapshots()
        .map(
          (snap) => snap.docs.map((doc) {
            final d = doc.data();
            final urls = (d['photoUrls'] as List<dynamic>?)
                    ?.whereType<String>()
                    .toList() ??
                const <String>[];
            final statusRaw = (d['status'] as String?) ?? PartStatus.available.name;
            final status = PartStatus.values.firstWhere(
              (e) => e.name == statusRaw,
              orElse: () => PartStatus.available,
            );
            final paymentRaw = d['paymentMethod'] as String?;
            final payment = paymentRaw == null
                ? null
                : PaymentMethod.values.firstWhere(
                    (e) => e.name == paymentRaw,
                    orElse: () => PaymentMethod.cash,
                  );
            return YardPart(
              id: doc.id,
              reference: (d['reference'] as String?) ?? doc.id.toUpperCase(),
              vehicleId: (d['vehicleId'] as String?) ?? '',
              name: (d['name'] as String?) ?? 'Pieza',
              category: (d['category'] as String?) ?? 'Motor',
              subtitle: (d['subtitle'] as String?) ?? '',
              location: (d['location'] as String?) ?? '',
              priceMxn: (d['priceMxn'] as num?)?.toInt(),
              status: status,
              photoPaths: urls,
              photoCount: (d['photoCount'] as num?)?.toInt() ?? urls.length,
              soldBy: d['soldBy'] as String?,
              paymentMethod: payment,
            );
          }).toList(),
        );
  }

  Future<List<String>> uploadPhotos({
    required String folder,
    required String entityId,
    required List<String> localPaths,
  }) async {
    final uploaded = <String>[];
    final now = DateTime.now().millisecondsSinceEpoch;
    for (var i = 0; i < localPaths.length; i++) {
      final path = localPaths[i];
      if (path.startsWith('http://') || path.startsWith('https://')) {
        uploaded.add(path);
        continue;
      }
      try {
        final bytes = await XFile(path).readAsBytes();
        final ref = _storage.ref('$folder/$entityId/${now}_$i.jpg');
        await ref.putData(
          bytes,
          SettableMetadata(contentType: 'image/jpeg'),
        );
        final url = await ref.getDownloadURL();
        uploaded.add(url);
      } catch (_) {
        // Keep local path fallback if offline or storage not enabled yet.
        uploaded.add(path);
      }
    }
    return uploaded;
  }

  Future<void> saveVehicle(YardVehicle vehicle) async {
    final photoUrls = await uploadPhotos(
      folder: 'vehicles',
      entityId: vehicle.id,
      localPaths: vehicle.photoPaths,
    );
    await _vehiclesCol.doc(vehicle.id).set({
      'reference': vehicle.reference,
      'year': vehicle.year,
      'make': vehicle.make,
      'model': vehicle.model,
      'color': vehicle.color,
      'location': vehicle.location,
      'photoUrls': photoUrls,
      'createdAt': FieldValue.serverTimestamp(),
    });
  }

  Future<void> savePart(YardPart part) async {
    final photoUrls = await uploadPhotos(
      folder: 'parts',
      entityId: part.id,
      localPaths: part.photoPaths,
    );
    await _partsCol.doc(part.id).set({
      'reference': part.reference,
      'vehicleId': part.vehicleId,
      'name': part.name,
      'category': part.category,
      'subtitle': part.subtitle,
      'location': part.location,
      'priceMxn': part.priceMxn,
      'status': part.status.name,
      'photoUrls': photoUrls,
      'photoCount': photoUrls.isNotEmpty ? photoUrls.length : part.photoCount,
      'soldBy': part.soldBy,
      'paymentMethod': part.paymentMethod?.name,
      'createdAt': FieldValue.serverTimestamp(),
      'updatedAt': FieldValue.serverTimestamp(),
    });
  }

  Future<String> attachPartPhoto(String partId, String localPath) async {
    final urls = await uploadPhotos(
      folder: 'parts',
      entityId: partId,
      localPaths: [localPath],
    );
    final url = urls.first;
    await _partsCol.doc(partId).update({
      'photoUrls': FieldValue.arrayUnion([url]),
      'photoCount': FieldValue.increment(1),
      'updatedAt': FieldValue.serverTimestamp(),
    });
    return url;
  }

  /// Atomic Firestore transaction to prevent double-selling a part.
  Future<bool> sellPartAtomic({
    required String partId,
    required String soldBy,
    required PaymentMethod paymentMethod,
  }) async {
    final ref = _partsCol.doc(partId);
    return _firestore.runTransaction((tx) async {
      final snap = await tx.get(ref);
      if (!snap.exists) return false;
      final data = snap.data();
      if (data?['status'] == PartStatus.sold.name) return false;
      tx.update(ref, {
        'status': PartStatus.sold.name,
        'soldBy': soldBy,
        'paymentMethod': paymentMethod.name,
        'soldAt': FieldValue.serverTimestamp(),
        'updatedAt': FieldValue.serverTimestamp(),
      });
      return true;
    });
  }

  Future<void> restorePart(String partId) async {
    await _partsCol.doc(partId).update({
      'status': PartStatus.available.name,
      'soldBy': null,
      'paymentMethod': null,
      'updatedAt': FieldValue.serverTimestamp(),
    });
  }

  Future<void> addStaffMember(StaffMember member) async {
    await _staffCol.doc(member.id).set({
      'name': member.name,
      'role': member.role.name,
      'roleSummary': member.roleSummary,
      'pin': member.pin,
      'createdAt': FieldValue.serverTimestamp(),
    });
  }

  Future<void> saveEnrollmentCode(String code) async {
    final expiresAt = DateTime.now().add(const Duration(minutes: 10));
    await _codesCol.doc(code).set({
      'code': code,
      'active': true,
      'expiresAt': Timestamp.fromDate(expiresAt),
      'createdAt': FieldValue.serverTimestamp(),
    });
  }

  Future<void> registerDevice({
    required String code,
    required String deviceName,
  }) async {
    await _devicesCol.add({
      'name': deviceName,
      'enrollmentCode': code,
      'linkedAt': FieldValue.serverTimestamp(),
    });
  }
}
