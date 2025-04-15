package com.example.kanjistudy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjistudy.data.StudyUnit
import com.example.kanjistudy.viewmodel.KanjiViewModel
import com.example.kanjistudy.viewmodel.KanjiViewModelFactory

@Composable
fun StudyUnitInputScreen( // Renamed
    onBack: () -> Unit,
    viewModel: KanjiViewModel = viewModel(factory = KanjiViewModelFactory())
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Add New Unit", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Unit Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
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
                    val unit = StudyUnit( // Updated type
                        name = name.trim(),
                        description = description.trim()
                    )
                    viewModel.uploadUnit(unit)
                    onBack()
                },
                enabled = name.isNotBlank()
            ) {
                Text("Upload")
            }
        }
    }
}