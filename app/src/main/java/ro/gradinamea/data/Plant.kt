package ro.gradinamea.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
O planta din gradina utilizatorului (tabelul "plants" din Room).

Data ultimei udari e salvata ca "epoch day" (numarul de zile de la 1 ian. 1970),
pentru ca Room nu stie sa salveze direct un LocalDate fara un TypeConverter.
 */
@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val species: String = "",
    val room: String,
    val isOutdoor: Boolean = false,
    val wateringIntervalDays: Int,
    val lastWateredEpochDay: Long,
    val notes: String = "",
    /** Poza plantei */
    val photoUri: String? = null,
)
