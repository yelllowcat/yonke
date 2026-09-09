package mx.yonkefacil.inventory.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.yonkefacil.inventory.data.local.LocalDatabase
import mx.yonkefacil.inventory.data.local.PendingMutationEntity
import mx.yonkefacil.inventory.data.local.SyncWorker
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.UUID

class NetworkInventoryRepository(
    private val context: Context,
    private val apiBaseUrl: String,
) : InventoryRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val preferences = context.getSharedPreferences("yonke_session", Context.MODE_PRIVATE)
    private val dao = LocalDatabase.get(context).inventoryDao()
    private val mutableVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    private val mutableParts = MutableStateFlow<List<Part>>(emptyList())
    private val mutableStaff = MutableStateFlow<List<StaffUser>>(emptyList())
    private val mutableEnrolled = MutableStateFlow(!preferences.getString("device_credential", null).isNullOrBlank())

    override val vehicles: StateFlow<List<Vehicle>> = mutableVehicles.asStateFlow()
    override val parts: StateFlow<List<Part>> = mutableParts.asStateFlow()
    override val staff: StateFlow<List<StaffUser>> = mutableStaff.asStateFlow()
    override val isEnrolled: StateFlow<Boolean> = mutableEnrolled.asStateFlow()

    init {
        preferences.edit().putString("api_base_url", apiBaseUrl.trimEnd('/')).apply()
        scope.launch {
            if (mutableEnrolled.value) refreshStaff()
            if (!session().isNullOrBlank()) refreshInventory()
        }
    }

    override suspend fun enrollDevice(code: String, deviceName: String): SaveResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().put("code", code).put("deviceName", deviceName)
            val result = request("/device-enrollments/redeem", "POST", payload)
            if (result.status !in 200..299) return@withContext SaveResult.Failed(result.message())
            val credential = result.body.optString("credential")
            if (credential.isBlank()) return@withContext SaveResult.Failed("Enrollment credential missing")
            preferences.edit().putString("device_credential", credential).apply()
            mutableEnrolled.value = true
            refreshStaff()
            SaveResult.Saved(Unit)
        } catch (error: Exception) {
            SaveResult.Failed(error.message ?: "Device enrollment failed")
        }
    }

    override suspend fun createEnrollmentCode(): SaveResult<String> = withContext(Dispatchers.IO) {
        try {
            val result = authenticatedRequest("/device-enrollments", "POST", JSONObject())
            if (result.status !in 200..299) SaveResult.Failed(result.message())
            else SaveResult.Saved(result.body.getString("code"))
        } catch (error: Exception) {
            SaveResult.Failed(error.message ?: "Enrollment code could not be created")
        }
    }

    override suspend fun addStaff(displayName: String, pin: String): SaveResult<StaffUser> = withContext(Dispatchers.IO) {
        try {
            val result = authenticatedRequest(
                "/staff", "POST", JSONObject().put("displayName", displayName).put("pin", pin).put("role", "staff"),
            )
            if (result.status !in 200..299) return@withContext SaveResult.Failed(result.message())
            val member = result.body.getJSONObject("staff").toStaff()
            mutableStaff.value += member
            SaveResult.Saved(member)
        } catch (error: Exception) {
            SaveResult.Failed(error.message ?: "Staff member could not be created")
        }
    }

    override suspend fun signIn(staffId: String, pin: String): StaffUser? = withContext(Dispatchers.IO) {
        try {
            val result = request(
                "/staff-sessions",
                "POST",
                JSONObject().put("staffId", staffId).put("pin", pin),
                mapOf("X-Device-Credential" to (deviceCredential() ?: return@withContext null)),
            )
            if (result.status !in 200..299) return@withContext null
            val userJson = result.body.getJSONObject("staff")
            val user = userJson.toStaff()
            preferences.edit().putString("staff_session", result.body.getString("token")).apply()
            refreshInventory()
            user
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun addVehicle(input: NewVehicle): SaveResult<Vehicle> = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("year", input.year)
            .put("make", input.make)
            .put("model", input.model)
            .put("color", input.color)
            .put("vin", input.vin)
            .put("locationNote", input.locationNote)
        val requestId = UUID.randomUUID().toString()
        try {
            val result = authenticatedRequest("/vehicles", "POST", payload, requestId)
            if (result.status !in 200..299) return@withContext SaveResult.Failed(result.message())
            val vehicle = result.body.getJSONObject("vehicle").toVehicle()
            mutableVehicles.value = listOf(vehicle) + mutableVehicles.value
            SaveResult.Saved(vehicle)
        } catch (_: Exception) {
            queue(requestId, "/vehicles", payload)
            SaveResult.Queued
        }
    }

    override suspend fun addPart(input: NewPart): SaveResult<Part> = withContext(Dispatchers.IO) {
        try {
            val photo = input.photoPath?.let { uploadPhoto(it) }
            if (input.published && photo == null) return@withContext SaveResult.Failed("A published part needs a photo")
            val payload = JSONObject()
                .put("donorVehicleId", input.vehicleId)
                .put("nameEs", input.nameEs)
                .put("nameEn", input.nameEn)
                .put("categoryEs", input.categoryEs)
                .put("categoryEn", input.categoryEn)
                .put("condition", input.condition.apiValue())
                .put("locationNote", input.locationNote)
                .put("priceMxnCents", input.priceMxn?.times(100) ?: JSONObject.NULL)
                .put("isPublished", input.published)
            if (photo != null) payload.put("photo", photo)
            val requestId = UUID.randomUUID().toString()
            val result = authenticatedRequest("/parts", "POST", payload, requestId)
            if (result.status !in 200..299) return@withContext SaveResult.Failed(result.message())
            val part = result.body.getJSONObject("part").toPart()
            mutableParts.value = listOf(part) + mutableParts.value
            SaveResult.Saved(part)
        } catch (error: Exception) {
            SaveResult.Failed(error.message ?: "Part could not be saved")
        }
    }

    override suspend fun sellPart(partId: String, staffId: String): SaveResult<Part> = withContext(Dispatchers.IO) {
        try {
            val result = authenticatedRequest("/parts/$partId/sell", "POST", JSONObject(), UUID.randomUUID().toString())
            if (result.status !in 200..299) return@withContext SaveResult.Failed(result.message())
            val sold = result.body.getJSONObject("part").toPart()
            mutableParts.value = mutableParts.value.map { if (it.id == sold.id) sold else it }
            SaveResult.Saved(sold)
        } catch (error: Exception) {
            SaveResult.Failed(error.message ?: "A connection is required to sell")
        }
    }

    override suspend fun restorePart(partId: String): SaveResult<Part> = withContext(Dispatchers.IO) {
        try {
            val result = authenticatedRequest("/parts/$partId/restore", "POST", JSONObject())
            if (result.status !in 200..299) return@withContext SaveResult.Failed(result.message())
            val restored = result.body.getJSONObject("part").toPart()
            mutableParts.value = mutableParts.value.map { if (it.id == restored.id) restored else it }
            SaveResult.Saved(restored)
        } catch (error: Exception) {
            SaveResult.Failed(error.message ?: "Part could not be restored")
        }
    }

    private suspend fun refreshStaff() = withContext(Dispatchers.IO) {
        try {
            val credential = deviceCredential() ?: return@withContext
            val result = request("/staff", headers = mapOf("X-Device-Credential" to credential))
            if (result.status in 200..299) mutableStaff.value = result.body.getJSONArray("staff").objects().map { it.toStaff() }
        } catch (_: Exception) {
            // Keep the last staff list available during a short outage.
        }
    }

    private suspend fun refreshInventory() = withContext(Dispatchers.IO) {
        try {
            val vehicleResult = authenticatedRequest("/vehicles")
            val partResult = authenticatedRequest("/parts")
            if (vehicleResult.status in 200..299) mutableVehicles.value = vehicleResult.body.getJSONArray("vehicles").objects().map { it.toVehicle() }
            if (partResult.status in 200..299) mutableParts.value = partResult.body.getJSONArray("parts").objects().map { it.toPart() }
        } catch (_: Exception) {
            // Local drafts/outbox remain available; the UI can retry sign-in or refresh later.
        }
    }

    private fun uploadPhoto(path: String): JSONObject {
        val sign = authenticatedRequestBlocking("/uploads/sign", "POST", JSONObject())
        if (sign.status !in 200..299) throw IllegalStateException(sign.message())
        val signedUrl = sign.body.getString("signedUrl").let { value ->
            if (value.startsWith("http")) value else {
                val uri = URI(apiBaseUrl)
                "${uri.scheme}://${uri.authority}${if (value.startsWith('/')) value else "/$value"}"
            }
        }
        val file = File(path)
        val connection = URL(signedUrl).openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.connectTimeout = 12_000
        connection.readTimeout = 12_000
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "image/jpeg")
        file.inputStream().use { input -> connection.outputStream.use { output -> input.copyTo(output) } }
        val status = connection.responseCode
        connection.disconnect()
        if (status !in 200..299) throw IllegalStateException("Photo upload failed")
        return JSONObject().put("storagePath", sign.body.getString("storagePath")).put("publicUrl", sign.body.getString("publicUrl"))
    }

    private suspend fun queue(requestId: String, route: String, payload: JSONObject) {
        dao.enqueue(PendingMutationEntity(requestId, route, "POST", payload.toString()))
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val work = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(constraints).build()
        WorkManager.getInstance(context).enqueueUniqueWork("inventory-sync", ExistingWorkPolicy.KEEP, work)
    }

    private fun authenticatedRequestBlocking(path: String, method: String = "GET", payload: JSONObject? = null, requestId: String? = null): HttpResult {
        val token = session() ?: throw IllegalStateException("Sign in to continue")
        val headers = mutableMapOf("X-Staff-Session" to token)
        if (requestId != null) headers["X-Request-Id"] = requestId
        return requestBlocking(path, method, payload, headers)
    }

    private suspend fun authenticatedRequest(path: String, method: String = "GET", payload: JSONObject? = null, requestId: String? = null): HttpResult =
        withContext(Dispatchers.IO) { authenticatedRequestBlocking(path, method, payload, requestId) }

    private suspend fun request(path: String, method: String = "GET", payload: JSONObject? = null, headers: Map<String, String> = emptyMap()): HttpResult =
        withContext(Dispatchers.IO) { requestBlocking(path, method, payload, headers) }

    private fun requestBlocking(path: String, method: String, payload: JSONObject?, headers: Map<String, String>): HttpResult {
        val connection = (URL(apiBaseUrl.trimEnd('/') + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 8_000
            readTimeout = 8_000
            setRequestProperty("Accept", "application/json")
            headers.forEach(::setRequestProperty)
            if (payload != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
        }
        payload?.let { connection.outputStream.use { stream -> stream.write(it.toString().toByteArray()) } }
        val status = connection.responseCode
        val text = (if (status in 200..299) connection.inputStream else connection.errorStream)?.bufferedReader()?.use { it.readText() }.orEmpty()
        connection.disconnect()
        return HttpResult(status, if (text.isBlank()) JSONObject() else JSONObject(text))
    }

    private fun deviceCredential() = preferences.getString("device_credential", null)
    private fun session() = preferences.getString("staff_session", null)

    private data class HttpResult(val status: Int, val body: JSONObject) {
        fun message(): String = body.optJSONObject("error")?.optString("message")?.takeIf(String::isNotBlank) ?: "Request failed ($status)"
    }

    private fun JSONObject.toStaff() = StaffUser(
        id = optString("id", optString("staff_id")),
        name = optString("name", optString("display_name")),
        role = if (optString("role") == "owner") StaffRole.Owner else StaffRole.Staff,
    )

    private fun JSONObject.toVehicle() = Vehicle(
        id = getString("id"),
        reference = getString("reference"),
        year = getInt("year"),
        make = getString("make"),
        model = getString("model"),
        color = optString("color"),
        vin = optString("vin"),
        locationNote = optString("location_note"),
        revision = optInt("revision", 1),
    )

    private fun JSONObject.toPart() = Part(
        id = getString("id"),
        reference = getString("reference"),
        vehicleId = getString("donor_vehicle_id"),
        nameEs = getString("name_es"),
        nameEn = getString("name_en"),
        categoryEs = getString("category_es"),
        categoryEn = getString("category_en"),
        condition = conditionFromApi(getString("condition")),
        locationNote = optString("location_note"),
        priceMxn = if (isNull("price_mxn_cents")) null else getInt("price_mxn_cents") / 100,
        published = optBoolean("is_published"),
        hasPhoto = true,
        status = when (getString("status")) { "sold" -> InventoryStatus.Sold; "archived" -> InventoryStatus.Archived; else -> InventoryStatus.Available },
        soldBy = optString("sold_by").takeIf(String::isNotBlank),
        revision = optInt("revision", 1),
    )

    private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map(::getJSONObject)
    private fun PartCondition.apiValue() = when (this) {
        PartCondition.Tested -> "tested"
        PartCondition.Good -> "good"
        PartCondition.Untested -> "untested"
        PartCondition.CosmeticWear -> "cosmetic_wear"
        PartCondition.Damaged -> "damaged"
    }
    private fun conditionFromApi(value: String) = when (value) {
        "tested" -> PartCondition.Tested
        "good" -> PartCondition.Good
        "cosmetic_wear" -> PartCondition.CosmeticWear
        "damaged" -> PartCondition.Damaged
        else -> PartCondition.Untested
    }
}
