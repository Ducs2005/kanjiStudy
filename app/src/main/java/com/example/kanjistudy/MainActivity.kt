package com.example.kanjistudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kanjistudy.navigation.KanjiApp
import com.example.kanjistudy.ui.theme.KanjiStudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KanjiStudyTheme {
                KanjiApp()
            }
        }
    }
}