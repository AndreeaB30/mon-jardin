package ro.gradinamea.data

import java.time.LocalDate

/** Date de test, cu udari relative la ziua de azi ca sa se vada toate starile in lista */
object SampleData {
    fun plants(today: LocalDate = LocalDate.now()): List<Plant> {
        fun daysAgo(n: Long) = today.minusDays(n).toEpochDay()
        return listOf(
            Plant(name = "Monstera", species = "Monstera deliciosa", room = "Sufragerie",
                wateringIntervalDays = 7, lastWateredEpochDay = daysAgo(3),
                notes = "Nu-i place soarele direct."),
            Plant(name = "Ficus", species = "Ficus elastica", room = "Birou",
                wateringIntervalDays = 10, lastWateredEpochDay = daysAgo(10),
                notes = "Sterge frunzele de praf o data pe luna."),
            Plant(name = "Busuioc", species = "Ocimum basilicum", room = "Bucatarie",
                wateringIntervalDays = 2, lastWateredEpochDay = daysAgo(4),
                notes = "Ciupeste varfurile ca sa se indeseasca."),
            Plant(name = "Muscata", species = "Pelargonium", room = "Balcon", isOutdoor = true,
                wateringIntervalDays = 3, lastWateredEpochDay = daysAgo(2),
                notes = "O aduci în casa cand scade sub 5°C."),
            Plant(name = "Aloe vera", species = "Aloe barbadensis", room = "Dormitor",
                wateringIntervalDays = 21, lastWateredEpochDay = daysAgo(5),
                notes = "Prefera sa fie mai degraba uscata decât uda."),
            Plant(name = "Orhidee", species = "Phalaenopsis", room = "Sufragerie",
                wateringIntervalDays = 7, lastWateredEpochDay = daysAgo(6),
                notes = "Udare prin imersie, 10 minute."),
        )
    }
}
