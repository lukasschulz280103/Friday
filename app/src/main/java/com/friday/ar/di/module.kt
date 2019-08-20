package com.friday.ar.di

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.friday.ar.Theme
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        PreferenceManager.getDefaultSharedPreferences(get())
    }
    single {
        Theme(get())
    }
}