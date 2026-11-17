package ro.gradinamea.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ro.gradinamea.data.Plant
import ro.gradinamea.data.PlantRepository

data class PlantListUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true,
)

class PlantListViewModel(repository: PlantRepository) : ViewModel() {

    val uiState: StateFlow<PlantListUiState> = repository.getAllPlants()
        .map { PlantListUiState(plants = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlantListUiState(),
        )
}
