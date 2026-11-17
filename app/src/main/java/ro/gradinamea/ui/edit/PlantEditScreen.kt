package ro.gradinamea.ui.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ro.gradinamea.data.PlantRooms
import ro.gradinamea.ui.AppViewModelProvider
import ro.gradinamea.ui.components.formatRo
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantEditScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: PlantEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val form = viewModel.form
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (viewModel.isEditMode) "Editeaza planta" else "Planta noua")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.Close, contentDescription = "Anuleaza")
                    }
                },
            )
        },
    ) { innerPadding ->
        if (viewModel.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            OutlinedTextField(
                value = form.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nume *") },
                placeholder = { Text("ex. Monstera") },
                isError = form.nameError != null,
                supportingText = {
                    Text(form.nameError ?: "${form.name.length}/$MAX_NAME_LENGTH")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = form.species,
                onValueChange = viewModel::onSpeciesChange,
                label = { Text("Specie") },
                placeholder = { Text("ex. Monstera deliciosa") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            RoomDropdown(selected = form.room, onSelected = viewModel::onRoomChange)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sta afara", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Pe balcon sau în gradina",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = form.isOutdoor, onCheckedChange = viewModel::onOutdoorChange)
            }

            OutlinedTextField(
                value = form.intervalText,
                onValueChange = viewModel::onIntervalChange,
                label = { Text("Se uda o dată la (zile) *") },
                isError = form.intervalError != null,
                supportingText = {
                    Text(form.intervalError ?: "Intre $MIN_INTERVAL și $MAX_INTERVAL de zile")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            LastWateredField(date = form.lastWateredDate, onDateChange = viewModel::onLastWateredChange)

            OutlinedTextField(
                value = form.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notite") },
                placeholder = { Text("ex. Nu-i place soarele direct") },
                minLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    scope.launch { if (viewModel.save()) onSaved() }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (viewModel.isEditMode) "Salveaza modificarile" else "Adauga planta")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Camera") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PlantRooms.all.forEach { room ->
                DropdownMenuItem(
                    text = { Text(room) },
                    onClick = {
                        onSelected(room)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Camp doar pentru citire; la apasare se deschide calendarul. Nu se pot alege date din viitor. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LastWateredField(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = date.formatRo(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Ultima udare") },
            trailingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Alege data") },
            modifier = Modifier.fillMaxWidth(),
        )
        // Strat transparent peste câmp, ca tot câmpul să deschidă calendarul.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showPicker = true },
        )
    }

    if (showPicker) {
        val todayUtcMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayUtcMillis
            },
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onDateChange(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Anuleaza") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}
