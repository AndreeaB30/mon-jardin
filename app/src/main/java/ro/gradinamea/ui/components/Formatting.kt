package ro.gradinamea.ui.components

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ro"))

fun LocalDate.formatRo(): String = format(dateFormatter)

fun countText(n: Long, singular: String, plural: String): String {
    val lastTwo = n % 100
    return when {
        n == 1L -> "1 $singular"
        lastTwo == 0L || lastTwo >= 20 -> "$n de $plural"
        else -> "$n $plural"
    }
}

fun daysText(n: Long): String = countText(n, "zi", "zile")

fun plantsText(n: Int): String = countText(n.toLong(), "planta", "plante")
