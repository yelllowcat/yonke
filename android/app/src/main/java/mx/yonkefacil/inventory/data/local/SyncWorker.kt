package mx.yonkefacil.inventory.data.local

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SyncWorker(
    appContext: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(appContext, parameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val preferences = applicationContext.getSharedPreferences("yonke_session", Context.MODE_PRIVATE)
        val apiBaseUrl = preferences.getString("api_base_url", null) ?: return@withContext Result.success()
        val staffSession = preferences.getString("staff_session", null) ?: return@withContext Result.retry()
        val dao = LocalDatabase.get(applicationContext).inventoryDao()

        for (mutation in dao.pending()) {
            val connection = (URL(apiBaseUrl.trimEnd('/') + mutation.route).openConnection() as HttpURLConnection).apply {
                requestMethod = mutation.method
                connectTimeout = 8_000
                readTimeout = 8_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("X-Staff-Session", staffSession)
                setRequestProperty("X-Request-Id", mutation.requestId)
            }
            try {
                connection.outputStream.use { it.write(mutation.payloadJson.toByteArray()) }
                if (connection.responseCode in 200..299) dao.complete(mutation.requestId)
                else {
                    dao.recordAttempt(mutation.requestId)
                    if (connection.responseCode >= 500) return@withContext Result.retry()
                }
            } catch (_: Exception) {
                dao.recordAttempt(mutation.requestId)
                return@withContext Result.retry()
            } finally {
                connection.disconnect()
            }
        }
        Result.success()
    }
}

