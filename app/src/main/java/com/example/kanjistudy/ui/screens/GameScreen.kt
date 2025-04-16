package com.example.kanjistudy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjistudy.data.Kanji
import com.example.kanjistudy.viewmodel.KanjiViewModel
import com.example.kanjistudy.viewmodel.KanjiViewModelFactory
import kotlin.random.Random

@Composable
fun GameScreen(
    unitIds: List<String>,
    onBack: () -> Unit,
    viewModel: KanjiViewModel = viewModel(factory = KanjiViewModelFactory())
) {
    val allKanjiList by viewModel.allKanjiList.collectAsState(initial = emptyList())

    val gameKanjiList = if (unitIds.isNotEmpty()) {
        allKanjiList.filter { kanji -> kanji.unitId in unitIds }
    } else {
        allKanjiList
    }

    var currentKanji by remember { mutableStateOf<Kanji?>(null) }
    var userInput by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showDetails by remember { mutableStateOf(false) }
    var skipped by remember { mutableStateOf(false) } // Track if the Kanji was skipped

    LaunchedEffect(gameKanjiList, showDetails) {
        if (!showDetails && gameKanjiList.isNotEmpty()) {
            currentKanji = gameKanjiList[Random.nextInt(gameKanjiList.size)]
            userInput = ""
            isCorrect = null
            skipped = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kanji Reading Game",
            style = MaterialTheme.typography.headlineSmall
        )

        if (gameKanjiList.isEmpty()) {
            Text(text = "No Kanji available in the selected units. Please add some Kanji or select different units.")
        } else if (currentKanji == null) {
            CircularProgressIndicator()
        } else {
            Text(
                text = currentKanji!!.character,
                fontSize = 80.sp,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )

            if (showDetails) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (skipped) {
                            Text(
                                text = "Skipped!",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        } else {
                            Text(
                                text = "Correct!",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(text = "Meaning: ${currentKanji!!.meaning}")
                        Text(text = "Kun: ${currentKanji!!.kunReadings.joinToString(", ")}")
                        Text(text = "On: ${currentKanji!!.onReadings.joinToString(", ")}")
                        Text(text = "Examples: ${currentKanji!!.exampleWords.joinToString(", ")}")
                    }
                }
                Button(
                    onClick = {
                        showDetails = false // Move to the next Kanji
                    }
                ) {
                    Text("Next Kanji")
                }
            } else {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("Enter Kun or On Reading") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (isCorrect == false) {
                    Text(
                        text = "Incorrect! Please try again.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val correctKun = currentKanji!!.kunReadings.any { it.equals(userInput.trim(), ignoreCase = true) }
                            val correctOn = currentKanji!!.onReadings.any { it.equals(userInput.trim(), ignoreCase = true) }
                            isCorrect = correctKun || correctOn
                            if (isCorrect == true) {
                                showDetails = true
                            }
                        },
                        enabled = userInput.isNotBlank()
                    ) {
                        Text("Submit")
                    }
                    Button(
                        onClick = {
                            skipped = true
                            showDetails = true // Show the answer
                        }
                    ) {
                        Text("Skip")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}