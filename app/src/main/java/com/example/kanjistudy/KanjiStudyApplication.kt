package com.example.kanjistudy

import android.app.Application
import com.google.firebase.FirebaseApp
import android.util.Log

class KanjiStudyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase as early as possible
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp != null) {
            Log.d("KanjiStudyApplication", "Firebase initialized successfully")
        } else {
            Log.e("KanjiStudyApplication", "Firebase initialization failed")
        }
    }
}