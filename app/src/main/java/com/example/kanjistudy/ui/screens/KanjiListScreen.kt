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
import com.example.kanjistudy.data.Kanji
import com.example.kanjistudy.viewmodel.KanjiViewModel
import com.example.kanjistudy.viewmodel.KanjiViewModelFactory

@Composable
fun KanjiListScreen(
    unitId: String,
    onBack: () -> Unit,
    onAddKanji: (String) -> Unit,
    viewModel: KanjiViewModel = viewModel(factory = KanjiViewModelFactory())
) {
    val kanjiList by viewModel.kanjiList.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var selectedKanji by remember { mutableStateOf<Kanji?>(null) }
    var editedCharacter by remember { mutableStateOf("") }
    var editedKunReadings by remember { mutableStateOf("") }
    var editedOnReadings by remember { mutableStateOf("") }
    var editedMeaning by remember { mutableStateOf("") }
    var editedExampleWords by remember { mutableStateOf("") }

    LaunchedEffect(unitId) {
        viewModel.fetchKanjiListForUnit(unitId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Kanji in Unit", style = MaterialTheme.typography.headlineSmall)

        Button(
            onClick = { onAddKanji(unitId) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Kanji")
        }

        if (kanjiList.isEmpty()) {
            Text(text = "No Kanji found in this Unit.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(kanjiList) { kanji ->
                    KanjiItem(
                        kanji = kanji,
                        onClick = {
                            selectedKanji = kanji
                            editedCharacter = kanji.character
                            editedKunReadings = kanji.kunReadings.joinToString(" ")
                            editedOnReadings = kanji.onReadings.joinToString(" ")
                            editedMeaning = kanji.meaning
                            editedExampleWords = kanji.exampleWords.joinToString(", ")
                            showDialog = true
                        }
                    )
                }
            }
        }

        Button(onClick = onBack) {
            Text("Back")
        }
    }

    if (showDialog && selectedKanji != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Kanji") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editedCharacter,
                        onValueChange = { editedCharacter = it },
                        label = { Text("Kanji Character") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedKunReadings,
                        onValueChange = { editedKunReadings = it },
                        label = { Text("Kun Readings (separated by comma, space, or Japanese comma/space)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedOnReadings,
                        onValueChange = { editedOnReadings = it },
                        label = { Text("On Readings (separated by comma, space, or Japanese comma/space)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedMeaning,
                        onValueChange = { editedMeaning = it },
                        label = { Text("Meaning") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedExampleWords,
                        onValueChange = { editedExampleWords = it },
                        label = { Text("Example Words (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val updatedKanji = Kanji(
                                unitId = selectedKanji!!.unitId,
                                character = editedCharacter,
                                kunReadings = editedKunReadings.split(Regex("[,\\s　、]+")).map { it.trim() }.filter { it.isNotEmpty() },
                                onReadings = editedOnReadings.split(Regex("[,\\s　、]+")).map { it.trim() }.filter { it.isNotEmpty() },
                                meaning = editedMeaning,
                                exampleWords = editedExampleWords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                            selectedKanji?.let { kanji ->
                                viewModel.updateKanji(unitId, kanji.id ?: "", updatedKanji)
                            }
                            showDialog = false
                        },
                        enabled = editedCharacter.isNotBlank() && editedMeaning.isNotBlank()
                    ) {
                        Text("Save")
                    }
                    Button(
                        onClick = {
                            selectedKanji?.let { kanji ->
                                viewModel.deleteKanji(unitId, kanji.id ?: "")
                            }
                            showDialog = false
                        }
                    ) {
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun KanjiItem(kanji: Kanji, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Kanji: ${kanji.character}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Meaning: ${kanji.meaning}")
            Text(text = "Kun: ${kanji.kunReadings.joinToString(", ")}")
            Text(text = "On: ${kanji.onReadings.joinToString(", ")}")
            Text(text = "Examples: ${kanji.exampleWords.joinToString(", ")}")
        }
    }
}