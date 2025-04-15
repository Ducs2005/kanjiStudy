package com.example.kanjistudy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    onNavigateToUnitInput: () -> Unit,
    onNavigateToUnitSelection: () -> Unit, // Updated
    onNavigateToUnitList: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Kanji Study App")
        Button(onClick = onNavigateToUnitInput) {
            Text(text = "Add New Unit")
        }
        Button(onClick = onNavigateToUnitSelection) { // Updated
            Text(text = "Play Game")
        }
        Button(onClick = onNavigateToUnitList) {
            Text(text = "View Unit List")
        }
    }
}