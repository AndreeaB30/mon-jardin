package ro.gradinamea.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    /** Flow: lista se reemite automat dupa orice inserare, modificare sau stergere. */
    @Query("SELECT * FROM plants ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id")
    fun getById(id: Long): Flow<Plant?>

    @Insert
    suspend fun insert(plant: Plant): Long

    @Update
    suspend fun update(plant: Plant)

    @Delete
    suspend fun delete(plant: Plant)
}
