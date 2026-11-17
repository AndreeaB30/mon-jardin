package ro.gradinamea.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ro.gradinamea.GradinaMeaApplication
import ro.gradinamea.ui.detail.PlantDetailViewModel
import ro.gradinamea.ui.edit.PlantEditViewModel
import ro.gradinamea.ui.list.PlantListViewModel

/** Stie cum sa construiasca fiecare ViewModel (le da repository-ul si argumentele de navigare). */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { PlantListViewModel(gradinaApp().repository) }
        initializer { PlantDetailViewModel(createSavedStateHandle(), gradinaApp().repository) }
        initializer { PlantEditViewModel(createSavedStateHandle(), gradinaApp().repository) }
    }
}

private fun CreationExtras.gradinaApp(): GradinaMeaApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GradinaMeaApplication
