package ro.gradinamea

import android.app.Application
import ro.gradinamea.data.PlantDatabase
import ro.gradinamea.data.PlantRepository

/** Crearea repositoryului*/
class GradinaMeaApplication : Application() {
    val repository: PlantRepository by lazy {
        PlantRepository(PlantDatabase.getDatabase(this).plantDao())
    }
}
