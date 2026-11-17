package ro.gradinamea.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Camerele din care utilizatorul poate alege în formular. */
object PlantRooms {
    const val BALCONY = "Balcon"
    val all = listOf("Sufragerie", "Dormitor", "Bucatarie", "Birou", "Baie", BALCONY)
}

val Plant.lastWateredDate: LocalDate
    get() = LocalDate.ofEpochDay(lastWateredEpochDay)

val Plant.nextWateringDate: LocalDate
    get() = lastWateredDate.plusDays(wateringIntervalDays.toLong())

/** Cate zile mai sunt pana la urmatoarea udare (negativ = s-a depașit termenul). */
fun Plant.daysUntilWatering(today: LocalDate = LocalDate.now()): Long =
    ChronoUnit.DAYS.between(today, nextWateringDate)
