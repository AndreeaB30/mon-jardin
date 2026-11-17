package ro.gradinamea.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ro.gradinamea.data.Plant
import ro.gradinamea.data.daysUntilWatering
import ro.gradinamea.data.lastWateredDate
import ro.gradinamea.data.nextWateringDate
import ro.gradinamea.ui.AppViewModelProvider
import ro.gradinamea.ui.components.PlantAvatar
import ro.gradinamea.ui.components.WateringStatusChip
import ro.gradinamea.ui.components.daysText
import ro.gradinamea.ui.components.formatRo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: PlantDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val plant = uiState.plant
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(plant?.name ?: "Detalii planta", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Înapoi")
                    }
                },
                actions = {
                    if (plant != null) {
                        IconButton(onClick = { onEdit(plant.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editează")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) { CircularProgressIndicator() }

            plant == null -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) { Text("Planta nu mai exista.") }

            else -> PlantDetailContent(
                plant = plant,
                onWatered = {
                    viewModel.markWateredToday()
                    scope.launch { snackbarHostState.showSnackbar("${plant.name} a fost udată azi.") }
                },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (showDeleteDialog && plant != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text("Stergi planta?") },
            text = { Text("„${plant.name}” va fi stearsa definitiv din gradina ta.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    scope.launch {
                        viewModel.deletePlant()
                        onDeleted()
                    }
                }) { Text("Sterge", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Anuleaza") }
            },
        )
    }
}

@Composable
private fun PlantDetailContent(plant: Plant, onWatered: () -> Unit, modifier: Modifier = Modifier) {
    val daysUntil = plant.daysUntilWatering()
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Antet: iconiță, nume, specie, starea udării
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlantAvatar(isOutdoor = plant.isOutdoor, size = 72.dp)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(plant.name, style = MaterialTheme.typography.headlineSmall)
                if (plant.species.isNotBlank()) {
                    Text(
                        plant.species,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
                WateringStatusChip(daysUntil = daysUntil)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                InfoRow(Icons.Outlined.MeetingRoom, "Camera", plant.room)
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                InfoRow(Icons.Outlined.WbSunny, "Locație", if (plant.isOutdoor) "Afara" else "In casa")
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                InfoRow(Icons.Outlined.Schedule, "Se uda", "o dată la ${daysText(plant.wateringIntervalDays.toLong())}")
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                InfoRow(Icons.Outlined.Event, "Ultima udare", plant.lastWateredDate.formatRo())
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                InfoRow(Icons.Outlined.CalendarMonth, "Urmatoarea udare", plant.nextWateringDate.formatRo())
            }
        }

        if (plant.notes.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.AutoMirrored.Outlined.Notes, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Notite", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(plant.notes, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Button(
            onClick = onWatered,
            enabled = daysUntil < plant.wateringIntervalDays, // dezactivat daca a fost udata deja azi
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Filled.WaterDrop, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Am udat-o azi")
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
