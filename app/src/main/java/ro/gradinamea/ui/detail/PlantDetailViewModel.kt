package ro.gradinamea.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ro.gradinamea.data.Plant
import ro.gradinamea.data.PlantRepository
import ro.gradinamea.navigation.Routes
import java.time.LocalDate

data class PlantDetailUiState(
    val plant: Plant? = null,
    val isLoading: Boolean = true,
)

class PlantDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PlantRepository,
) : ViewModel() {

    /** Id-ul vine din ruta de navigare: detail/{plantId}. */
    private val plantId: Long = checkNotNull(savedStateHandle[Routes.ARG_PLANT_ID])

    val uiState: StateFlow<PlantDetailUiState> = repository.getPlant(plantId)
        .map { PlantDetailUiState(plant = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlantDetailUiState(),
        )

    fun markWateredToday() {
        val plant = uiState.value.plant ?: return
        viewModelScope.launch {
            repository.updatePlant(plant.copy(lastWateredEpochDay = LocalDate.now().toEpochDay()))
        }
    }

    suspend fun deletePlant() {
        uiState.value.plant?.let { repository.deletePlant(it) }
    }
}
