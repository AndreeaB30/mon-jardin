package ro.gradinamea.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Plant::class], version = 1, exportSchema = false)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var instance: PlantDatabase? = null

        /** Singleton: o singura instanta a bazei de date pentru toata aplicatia. */
        fun getDatabase(context: Context): PlantDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "gradina_mea.db"
                )
                    .addCallback(SeedCallback)
                    .build()
                    .also { instance = it }
            }
    }

    /** La prima creare a bazei de date, se adauga cateva plante de test. */
    private object SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            SampleData.plants().forEach { plant ->
                db.execSQL(
                    "INSERT INTO plants (name, species, room, isOutdoor, wateringIntervalDays, " +
                        "lastWateredEpochDay, notes) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    arrayOf(
                        plant.name,
                        plant.species,
                        plant.room,
                        if (plant.isOutdoor) 1 else 0,
                        plant.wateringIntervalDays,
                        plant.lastWateredEpochDay,
                        plant.notes,
                    )
                )
            }
        }
    }
}
