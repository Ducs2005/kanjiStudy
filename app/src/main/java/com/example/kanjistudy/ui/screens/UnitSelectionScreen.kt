package com.example.kanjistudy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjistudy.data.StudyUnit
import com.example.kanjistudy.viewmodel.KanjiViewModel
import com.example.kanjistudy.viewmodel.KanjiViewModelFactory

@Composable
fun UnitSelectionScreen(
    onBack: () -> Unit,
    onUnitsSelected: (List<String>) -> Unit, // Updated to pass a list of unit IDs
    viewModel: KanjiViewModel = viewModel(factory = KanjiViewModelFactory())
) {
    val unitList by viewModel.unitList.collectAsState(initial = emptyList())

    // State to track selected units
    val selectedUnits = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Select Units for Game", style = MaterialTheme.typography.headlineSmall)

        if (unitList.isEmpty()) {
            Text(text = "No Units found. Please add some Units first.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All Units" option
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedUnits.size == unitList.size) {
                                        // Deselect all
                                        selectedUnits.clear()
                                    } else {
                                        // Select all
                                        selectedUnits.clear()
                                        selectedUnits.addAll(unitList.mapNotNull { it.id })
                                    }
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "All Units", style = MaterialTheme.typography.titleMedium)
                                Text(text = "Play with Kanji from all units")
                            }
                            Checkbox(
                                checked = selectedUnits.size == unitList.size,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedUnits.clear()
                                        selectedUnits.addAll(unitList.mapNotNull { it.id })
                                    } else {
                                        selectedUnits.clear()
                                    }
                                }
                            )
                        }
                    }
                }

                // List of units with checkboxes
                items(unitList) { unit ->
                    unit.id?.let { unitId ->
                        StudyUnitItem(
                            unit = unit,
                            isSelected = unitId in selectedUnits,
                            onToggleSelection = {
                                if (unitId in selectedUnits) {
                                    selectedUnits.remove(unitId)
                                } else {
                                    selectedUnits.add(unitId)
                                }
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text("Back")
            }
            Button(
                onClick = {
                    if (selectedUnits.isNotEmpty()) {
                        onUnitsSelected(selectedUnits.toList())
                    }
                },
                enabled = selectedUnits.isNotEmpty()
            ) {
                Text("Start Game")
            }
        }
    }
}

@Composable
fun StudyUnitItem(
    unit: StudyUnit,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleSelection() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Unit: ${unit.name}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Description: ${unit.description}")
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelection() }
            )
        }
    }
}