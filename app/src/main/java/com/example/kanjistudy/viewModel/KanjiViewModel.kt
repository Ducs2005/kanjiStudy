package com.example.kanjistudy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kanjistudy.data.Kanji
import com.example.kanjistudy.data.StudyUnit
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class KanjiViewModel : ViewModel() {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val TAG = "KanjiViewModel"

    private val _unitList = MutableStateFlow<List<StudyUnit>>(emptyList())
    val unitList: StateFlow<List<StudyUnit>> = _unitList

    private val _kanjiList = MutableStateFlow<List<Kanji>>(emptyList())
    val kanjiList: StateFlow<List<Kanji>> = _kanjiList

    private val _allKanjiList = MutableStateFlow<List<Kanji>>(emptyList())
    val allKanjiList: StateFlow<List<Kanji>> = _allKanjiList

    init {
        fetchUnitList()
        fetchAllKanji()
    }

    fun fetchUnitList() {
        db.collection("units")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error fetching StudyUnit list", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val unitList = snapshot.documents.mapNotNull { doc ->
                        val unit = doc.toObject(StudyUnit::class.java)
                        unit?.copy(id = doc.id)
                    }
                    _unitList.value = unitList
                    Log.d(TAG, "Fetched ${unitList.size} StudyUnits")
                }
            }
    }

    fun fetchKanjiListForUnit(unitId: String) {
        db.collection("units")
            .document(unitId)
            .collection("kanji")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error fetching Kanji list for StudyUnit $unitId", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val kanjiList = snapshot.documents.mapNotNull { doc ->
                        val kanji = doc.toObject(Kanji::class.java)
                        kanji?.copy(id = doc.id, unitId = unitId)
                    }
                    _kanjiList.value = kanjiList
                    Log.d(TAG, "Fetched ${kanjiList.size} Kanji for StudyUnit $unitId")
                }
            }
    }

    fun fetchAllKanji() {
        db.collectionGroup("kanji")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error fetching all Kanji", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val allKanji = snapshot.documents.mapNotNull { doc ->
                        val kanji = doc.toObject(Kanji::class.java)
                        val unitId = doc.reference.parent.parent?.id
                        kanji?.copy(id = doc.id, unitId = unitId)
                    }
                    _allKanjiList.value = allKanji
                    Log.d(TAG, "Fetched ${allKanji.size} Kanji (total)")
                }
            }
    }

    suspend fun checkKanjiExists(unitId: String, character: String): Boolean {
        return try {
            val querySnapshot = db.collection("units")
                .document(unitId)
                .collection("kanji")
                .whereEqualTo("character", character)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if Kanji exists: $character in StudyUnit $unitId", e)
            false
        }
    }

    fun uploadUnit(unit: StudyUnit) {
        db.collection("units")
            .add(unit)
            .addOnSuccessListener {
                Log.d(TAG, "StudyUnit uploaded successfully: ${unit.name}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error uploading StudyUnit: ${unit.name}", e)
            }
    }

    fun uploadKanji(unitId: String, kanji: Kanji) {
        db.collection("units")
            .document(unitId)
            .collection("kanji")
            .add(kanji)
            .addOnSuccessListener {
                Log.d(TAG, "Kanji uploaded successfully: ${kanji.character} to StudyUnit $unitId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error uploading Kanji: ${kanji.character} to StudyUnit $unitId", e)
            }
    }

    fun updateKanji(unitId: String, kanjiId: String, updatedKanji: Kanji) {
        db.collection("units")
            .document(unitId)
            .collection("kanji")
            .document(kanjiId)
            .set(updatedKanji)
            .addOnSuccessListener {
                Log.d(TAG, "Kanji updated successfully: ${updatedKanji.character}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating Kanji: ${updatedKanji.character}", e)
            }
    }

    fun deleteKanji(unitId: String, kanjiId: String) {
        db.collection("units")
            .document(unitId)
            .collection("kanji")
            .document(kanjiId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Kanji deleted successfully: $kanjiId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting Kanji: $kanjiId", e)
            }
    }

    fun deleteUnit(unitId: String) {
        // Use a batch to delete the unit and its kanji subcollection
        val batch = db.batch()

        // Delete all Kanji in the subcollection
        db.collection("units")
            .document(unitId)
            .collection("kanji")
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    batch.delete(document.reference)
                }

                // Delete the unit document itself
                val unitRef = db.collection("units").document(unitId)
                batch.delete(unitRef)

                // Commit the batch
                batch.commit()
                    .addOnSuccessListener {
                        Log.d(TAG, "StudyUnit deleted successfully: $unitId")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error deleting StudyUnit: $unitId", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching Kanji for deletion in StudyUnit: $unitId", e)
            }
    }
}

class KanjiViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KanjiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KanjiViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}