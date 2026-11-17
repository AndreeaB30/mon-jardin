package ro.gradinamea.ui.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ro.gradinamea.data.Plant
import ro.gradinamea.data.PlantRepository
import ro.gradinamea.data.PlantRooms
import ro.gradinamea.navigation.Routes
import java.time.LocalDate

const val MAX_NAME_LENGTH = 40
const val MIN_INTERVAL = 1
const val MAX_INTERVAL = 60

/** Ce e scris în formular. Intervalul e text, ca utilizatorul să poată sterge cifrele în timp ce scrie. */
data class PlantFormState(
    val name: String = "",
    val species: String = "",
    val room: String = PlantRooms.all.first(),
    val isOutdoor: Boolean = false,
    val intervalText: String = "7",
    val lastWateredDate: LocalDate = LocalDate.now(),
    val notes: String = "",
    val nameError: String? = null,
    val intervalError: String? = null,
)

class PlantEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PlantRepository,
) : ViewModel() {

    private val plantId: Long = savedStateHandle.get<Long>(Routes.ARG_PLANT_ID) ?: Routes.NO_PLANT
    val isEditMode: Boolean = plantId != Routes.NO_PLANT

    /** Planta așa cum era înainte de editare (ca sa pastram id-ul și poza). */
    private var originalPlant: Plant? = null

    var form by mutableStateOf(PlantFormState())
        private set

    var isLoading by mutableStateOf(isEditMode)
        private set

    init {
        if (isEditMode) {
            viewModelScope.launch {
                val plant = repository.getPlant(plantId).filterNotNull().first()
                originalPlant = plant
                form = PlantFormState(
                    name = plant.name,
                    species = plant.species,
                    room = plant.room,
                    isOutdoor = plant.isOutdoor,
                    intervalText = plant.wateringIntervalDays.toString(),
                    lastWateredDate = LocalDate.ofEpochDay(plant.lastWateredEpochDay),
                    notes = plant.notes,
                )
                isLoading = false
            }
        }
    }

    fun onNameChange(value: String) {
        if (value.length <= MAX_NAME_LENGTH) form = form.copy(name = value, nameError = null)
    }

    fun onSpeciesChange(value: String) {
        form = form.copy(species = value)
    }

    fun onRoomChange(value: String) {
        // Balconul e afara, așa că potrivim automat comutatorul (se poate schimba și manual).
        val isOutdoor = when {
            value == PlantRooms.BALCONY -> true
            form.room == PlantRooms.BALCONY -> false
            else -> form.isOutdoor
        }
        form = form.copy(room = value, isOutdoor = isOutdoor)
    }

    fun onOutdoorChange(value: Boolean) {
        form = form.copy(isOutdoor = value)
    }

    fun onIntervalChange(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) form = form.copy(intervalText = value, intervalError = null)
    }

    fun onLastWateredChange(date: LocalDate) {
        form = form.copy(lastWateredDate = date)
    }

    fun onNotesChange(value: String) {
        form = form.copy(notes = value)
    }

    private fun validate(): Boolean {
        val nameError = if (form.name.isBlank()) "Numele este obligatoriu" else null
        val interval = form.intervalText.toIntOrNull()
        val intervalError = if (interval == null || interval !in MIN_INTERVAL..MAX_INTERVAL) {
            "Introdu un numar între $MIN_INTERVAL și $MAX_INTERVAL"
        } else null
        form = form.copy(nameError = nameError, intervalError = intervalError)
        return nameError == null && intervalError == null
    }

    /** Intoarce true daca planta a fost salvata, false dacă formularul are erori. */
    suspend fun save(): Boolean {
        if (!validate()) return false
        val base = originalPlant ?: Plant(name = "", room = "", wateringIntervalDays = 0, lastWateredEpochDay = 0)
        val plant = base.copy(
            name = form.name.trim(),
            species = form.species.trim(),
            room = form.room,
            isOutdoor = form.isOutdoor,
            wateringIntervalDays = form.intervalText.toInt(),
            lastWateredEpochDay = form.lastWateredDate.toEpochDay(),
            notes = form.notes.trim(),
        )
        if (isEditMode) repository.updatePlant(plant) else repository.addPlant(plant)
        return true
    }
}
