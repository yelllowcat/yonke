package mx.yonkefacil.inventory.data

import kotlinx.coroutines.flow.StateFlow

interface InventoryRepository {
    val vehicles: StateFlow<List<Vehicle>>
    val parts: StateFlow<List<Part>>
    val staff: StateFlow<List<StaffUser>>
    val isEnrolled: StateFlow<Boolean>

    suspend fun enrollDevice(code: String, deviceName: String): SaveResult<Unit>
    suspend fun createEnrollmentCode(): SaveResult<String>
    suspend fun addStaff(displayName: String, pin: String): SaveResult<StaffUser>
    suspend fun signIn(staffId: String, pin: String): StaffUser?
    suspend fun addVehicle(input: NewVehicle): SaveResult<Vehicle>
    suspend fun addPart(input: NewPart): SaveResult<Part>
    suspend fun sellPart(partId: String, staffId: String): SaveResult<Part>
    suspend fun restorePart(partId: String): SaveResult<Part>
}
