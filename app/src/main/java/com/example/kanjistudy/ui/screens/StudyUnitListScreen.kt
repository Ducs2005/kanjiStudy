package com.example.kanjistudy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjistudy.data.StudyUnit
import com.example.kanjistudy.viewmodel.KanjiViewModel
import com.example.kanjistudy.viewmodel.KanjiViewModelFactory

@Composable
fun StudyUnitListScreen(
    onBack: () -> Unit,
    onUnitClick: (String) -> Unit,
    viewModel: KanjiViewModel = viewModel(factory = KanjiViewModelFactory())
) {
    val unitList by viewModel.unitList.collectAsState(initial = emptyList())

    // State for the action dialog (View/Delete options)
    var showActionDialog by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf<StudyUnit?>(null) }

    // State for the delete confirmation dialog
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Unit List", style = MaterialTheme.typography.headlineSmall)

        if (unitList.isEmpty()) {
            Text(text = "No Units found.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(unitList) { unit ->
                    StudyUnitItem(
                        unit = unit,
                        onClick = {
                            selectedUnit = unit
                            showActionDialog = true
                        }
                    )
                }
            }
        }

        Button(onClick = onBack) {
            Text("Back")
        }
    }

    // Action Dialog (View/Delete options)
    if (showActionDialog && selectedUnit != null) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Unit: ${selectedUnit!!.name}") },
            text = { Text("Choose an action for this unit.") },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            selectedUnit?.id?.let { onUnitClick(it) }
                            showActionDialog = false
                        }
                    ) {
                        Text("View Kanji")
                    }
                    Button(
                        onClick = {
                            // Show the delete confirmation dialog instead of deleting immediately
                            showDeleteConfirmation = true
                            showActionDialog = false
                        }
                    ) {
                        Text("Delete Unit")
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showActionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation && selectedUnit != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${selectedUnit!!.name}'? This will also delete all associated Kanji and cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUnit?.id?.let { unitId ->
                            viewModel.deleteUnit(unitId)
                        }
                        showDeleteConfirmation = false
                        selectedUnit = null // Clear the selected unit
                    }
                ) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudyUnitItem(unit: StudyUnit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Unit: ${unit.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Description: ${unit.description}")
        }
    }
}