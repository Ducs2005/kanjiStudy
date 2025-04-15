package com.example.kanjistudy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjistudy.data.Kanji
import com.example.kanjistudy.viewmodel.KanjiViewModel
import com.example.kanjistudy.viewmodel.KanjiViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun KanjiInputScreen(
    unitId: String,
    onBack: () -> Unit,
    viewModel: KanjiViewModel = viewModel(factory = KanjiViewModelFactory())
) {
    var character by remember { mutableStateOf("") }
    var kunReadings by remember { mutableStateOf("") }
    var onReadings by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var exampleWords by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Add Kanji to Unit", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = character,
            onValueChange = { character = it },
            label = { Text("Kanji Character") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = kunReadings,
            onValueChange = { kunReadings = it },
            label = { Text("Kun Readings (separated by comma, space, or Japanese comma/space)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = onReadings,
            onValueChange = { onReadings = it },
            label = { Text("On Readings (separated by comma, space, or Japanese comma/space)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = meaning,
            onValueChange = { meaning = it },
            label = { Text("Meaning") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = exampleWords,
            onValueChange = { exampleWords = it },
            label = { Text("Example Words (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text("Back")
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        val exists = viewModel.checkKanjiExists(unitId, character.trim())
                        if (exists) {
                            snackbarHostState.showSnackbar("Kanji '${character.trim()}' already exists in this Unit!")
                            character = ""
                            kunReadings = ""
                            onReadings = ""
                            meaning = ""
                            exampleWords = ""
                        } else {
                            val kanji = Kanji(
                                unitId = unitId,
                                character = character.trim(),
                                kunReadings = kunReadings.split(Regex("[,\\s　、]+")).map { it.trim() }.filter { it.isNotEmpty() },
                                onReadings = onReadings.split(Regex("[,\\s　、]+")).map { it.trim() }.filter { it.isNotEmpty() },
                                meaning = meaning.trim(),
                                exampleWords = exampleWords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                            viewModel.uploadKanji(unitId, kanji)
                            onBack()
                        }
                    }
                },
                enabled = character.isNotBlank() && meaning.isNotBlank()
            ) {
                Text("Upload")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(16.dp)
        )
    }
}