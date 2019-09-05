package com.friday.ar.core.di

import android.app.NotificationManager
import android.content.Context
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val coreModule = module {
    single { FirebaseStorage.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { FirebasePerformance.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { PreferenceManager.getDefaultSharedPreferences(get()) }
    single { androidApplication().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
}