package mx.yonkefacil.inventory.data

import java.util.UUID

enum class AppLanguage { Spanish, English }
enum class StaffRole { Owner, Staff }
enum class InventoryStatus { Available, Sold, Archived }
enum class PartCondition { Tested, Good, Untested, CosmeticWear, Damaged }

data class StaffUser(
    val id: String,
    val name: String,
    val role: StaffRole,
)

data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    val reference: String,
    val year: Int,
    val make: String,
    val model: String,
    val color: String = "",
    val vin: String = "",
    val locationNote: String = "",
    val revision: Int = 1,
)

data class Part(
    val id: String = UUID.randomUUID().toString(),
    val reference: String,
    val vehicleId: String,
    val nameEs: String,
    val nameEn: String,
    val categoryEs: String,
    val categoryEn: String,
    val condition: PartCondition,
    val locationNote: String,
    val priceMxn: Int?,
    val published: Boolean,
    val hasPhoto: Boolean,
    val status: InventoryStatus = InventoryStatus.Available,
    val soldBy: String? = null,
    val revision: Int = 1,
) {
    fun name(language: AppLanguage) = if (language == AppLanguage.Spanish) nameEs else nameEn
    fun category(language: AppLanguage) = if (language == AppLanguage.Spanish) categoryEs else categoryEn
}

data class NewVehicle(
    val year: Int,
    val make: String,
    val model: String,
    val color: String,
    val vin: String,
    val locationNote: String,
)

data class NewPart(
    val vehicleId: String,
    val nameEs: String,
    val nameEn: String,
    val categoryEs: String,
    val categoryEn: String,
    val condition: PartCondition,
    val locationNote: String,
    val priceMxn: Int?,
    val published: Boolean,
    val hasPhoto: Boolean,
    val photoPath: String? = null,
)

sealed interface SaveResult<out T> {
    data class Saved<T>(val value: T) : SaveResult<T>
    data object Queued : SaveResult<Nothing>
    data class Failed(val message: String) : SaveResult<Nothing>
}
