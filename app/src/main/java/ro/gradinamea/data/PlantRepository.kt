package ro.gradinamea.data

import kotlinx.coroutines.flow.Flow

/** Punctul prin care ViewModel-urile ajung la datele din PlantDao. */
class PlantRepository(private val plantDao: PlantDao) {
    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAll()
    fun getPlant(id: Long): Flow<Plant?> = plantDao.getById(id)
    suspend fun addPlant(plant: Plant): Long = plantDao.insert(plant)
    suspend fun updatePlant(plant: Plant) = plantDao.update(plant)
    suspend fun deletePlant(plant: Plant) = plantDao.delete(plant)
}
