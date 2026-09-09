package mx.yonkefacil.inventory.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DemoInventoryRepository : InventoryRepository {
    private val credentials = mapOf("staff-ana" to "1234", "owner-david" to "2468")
    override val staff = MutableStateFlow(
        listOf(
            StaffUser("staff-ana", "Ana", StaffRole.Staff),
            StaffUser("owner-david", "David", StaffRole.Owner),
        ),
    )
    override val isEnrolled = MutableStateFlow(true)

    private val mutableVehicles = MutableStateFlow(
        listOf(
            Vehicle("v-1001", "V-1001", 2012, "Nissan", "Sentra", "Plata", locationNote = "Junto a la entrada"),
            Vehicle("v-1002", "V-1002", 2015, "Chevrolet", "Aveo", "Blanco", locationNote = "Atrás del taller"),
            Vehicle("v-1003", "V-1003", 2018, "Kia", "Rio", "Rojo", locationNote = "Patio del fondo"),
        ),
    )
    override val vehicles: StateFlow<List<Vehicle>> = mutableVehicles.asStateFlow()

    private val mutableParts = MutableStateFlow(
        listOf(
            Part("p-1042", "P-1042", "v-1001", "Alternador", "Alternator", "Sistema eléctrico", "Electrical", PartCondition.Tested, "Estante junto al Sentra", 1450, true, true),
            Part("p-1047", "P-1047", "v-1002", "Faro delantero izquierdo", "Left headlight", "Carrocería", "Body", PartCondition.Good, "Caja azul del taller", 900, true, true),
            Part("p-1051", "P-1051", "v-1003", "Puerta trasera derecha", "Right rear door", "Carrocería", "Body", PartCondition.CosmeticWear, "Apoyada junto al Kia", null, true, true),
        ),
    )
    override val parts: StateFlow<List<Part>> = mutableParts.asStateFlow()

    override suspend fun enrollDevice(code: String, deviceName: String): SaveResult<Unit> = SaveResult.Saved(Unit)

    override suspend fun createEnrollmentCode(): SaveResult<String> = SaveResult.Saved("123456")

    override suspend fun addStaff(displayName: String, pin: String): SaveResult<StaffUser> {
        if (displayName.length < 2 || !pin.matches(Regex("\\d{4}"))) return SaveResult.Failed("Invalid staff")
        val member = StaffUser("staff-${staff.value.size + 1}", displayName.trim(), StaffRole.Staff)
        staff.value += member
        return SaveResult.Saved(member)
    }

    override suspend fun signIn(staffId: String, pin: String): StaffUser? =
        if (credentials[staffId] == pin) staff.value.firstOrNull { it.id == staffId } else null

    override suspend fun addVehicle(input: NewVehicle): SaveResult<Vehicle> {
        if (input.year !in 1900..2100 || input.make.isBlank() || input.model.isBlank()) {
            return SaveResult.Failed("Year, make and model are required")
        }
        val number = 1001 + mutableVehicles.value.size
        val vehicle = Vehicle(
            reference = "V-$number",
            year = input.year,
            make = input.make.trim(),
            model = input.model.trim(),
            color = input.color.trim(),
            vin = input.vin.trim(),
            locationNote = input.locationNote.trim(),
        )
        mutableVehicles.value += vehicle
        return SaveResult.Saved(vehicle)
    }

    override suspend fun addPart(input: NewPart): SaveResult<Part> {
        if (vehicles.value.none { it.id == input.vehicleId } || input.nameEs.isBlank() || input.nameEn.isBlank()) {
            return SaveResult.Failed("Vehicle and bilingual part names are required")
        }
        if (input.published && !input.hasPhoto) return SaveResult.Failed("A published part needs a photo")
        val number = 1042 + mutableParts.value.size * 5
        val part = Part(
            reference = "P-$number",
            vehicleId = input.vehicleId,
            nameEs = input.nameEs.trim(),
            nameEn = input.nameEn.trim(),
            categoryEs = input.categoryEs.trim(),
            categoryEn = input.categoryEn.trim(),
            condition = input.condition,
            locationNote = input.locationNote.trim(),
            priceMxn = input.priceMxn,
            published = input.published,
            hasPhoto = input.hasPhoto,
        )
        mutableParts.value += part
        return SaveResult.Saved(part)
    }

    override suspend fun sellPart(partId: String, staffId: String): SaveResult<Part> {
        val part = mutableParts.value.firstOrNull { it.id == partId } ?: return SaveResult.Failed("Part not found")
        if (part.status != InventoryStatus.Available) return SaveResult.Failed("Part is no longer available")
        val sold = part.copy(status = InventoryStatus.Sold, soldBy = staffId, revision = part.revision + 1)
        mutableParts.value = mutableParts.value.map { if (it.id == partId) sold else it }
        return SaveResult.Saved(sold)
    }

    override suspend fun restorePart(partId: String): SaveResult<Part> {
        val part = mutableParts.value.firstOrNull { it.id == partId } ?: return SaveResult.Failed("Part not found")
        if (part.status != InventoryStatus.Sold) return SaveResult.Failed("Only sold parts can be restored")
        val restored = part.copy(status = InventoryStatus.Available, soldBy = null, revision = part.revision + 1)
        mutableParts.value = mutableParts.value.map { if (it.id == partId) restored else it }
        return SaveResult.Saved(restored)
    }
}
