package ro.gradinamea.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ro.gradinamea.data.Plant
import ro.gradinamea.data.daysUntilWatering
import ro.gradinamea.ui.AppViewModelProvider
import ro.gradinamea.ui.components.PlantAvatar
import ro.gradinamea.ui.components.WateringStatusChip
import ro.gradinamea.ui.components.daysText
import ro.gradinamea.ui.components.plantsText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    onPlantClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    viewModel: PlantListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Gradina mea") },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Adauga planta") },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) { CircularProgressIndicator() }

            uiState.plants.isEmpty() -> EmptyState(Modifier.padding(innerPadding))

            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 88.dp, // loc pentru buton
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    Text(
                        text = plantsText(uiState.plants.size),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(uiState.plants, key = { it.id }) { plant ->
                    PlantCard(plant = plant, onClick = { onPlantClick(plant.id) })
                }
            }
        }
    }
}

@Composable
private fun PlantCard(plant: Plant, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp),
        ) {
            PlantAvatar(isOutdoor = plant.isOutdoor)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (plant.species.isNotBlank()) {
                    Text(
                        text = plant.species,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = "${plant.room} · la ${daysText(plant.wateringIntervalDays.toLong())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                WateringStatusChip(daysUntil = plant.daysUntilWatering())
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize().padding(32.dp),
    ) {
        Icon(
            Icons.Outlined.LocalFlorist,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text("Incă nu ai nicio planta", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "Apasa „Adauga planta” ca să începi gradina.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
