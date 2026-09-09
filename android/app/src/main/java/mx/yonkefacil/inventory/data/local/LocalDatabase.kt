package mx.yonkefacil.inventory.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey val key: String,
    val kind: String,
    val payloadJson: String,
    val updatedAtEpochMs: Long = System.currentTimeMillis(),
)

@Entity(tableName = "pending_mutations")
data class PendingMutationEntity(
    @PrimaryKey val requestId: String,
    val route: String,
    val method: String,
    val payloadJson: String,
    val attempts: Int = 0,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
)

@Dao
interface LocalInventoryDao {
    @Query("select * from drafts where key = :key")
    suspend fun draft(key: String): DraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: DraftEntity)

    @Query("delete from drafts where key = :key")
    suspend fun deleteDraft(key: String)

    @Query("select * from pending_mutations order by createdAtEpochMs")
    suspend fun pending(): List<PendingMutationEntity>

    @Query("select count(*) from pending_mutations")
    fun pendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun enqueue(mutation: PendingMutationEntity)

    @Query("delete from pending_mutations where requestId = :requestId")
    suspend fun complete(requestId: String)

    @Query("update pending_mutations set attempts = attempts + 1 where requestId = :requestId")
    suspend fun recordAttempt(requestId: String)
}

@Database(entities = [DraftEntity::class, PendingMutationEntity::class], version = 1, exportSchema = true)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun inventoryDao(): LocalInventoryDao

    companion object {
        @Volatile private var instance: LocalDatabase? = null

        fun get(context: Context): LocalDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                LocalDatabase::class.java,
                "yonke-facil-local.db",
            ).build().also { instance = it }
        }
    }
}

