package com.example.kanjistudy.data


data class Kanji(
    val id: String? = null,
    val unitId: String? = null, // Still named unitId for clarity (refers to StudyUnit ID)
    val character: String = "",
    val kunReadings: List<String> = emptyList(),
    val onReadings: List<String> = emptyList(),
    val meaning: String = "",
    val exampleWords: List<String> = emptyList()
)