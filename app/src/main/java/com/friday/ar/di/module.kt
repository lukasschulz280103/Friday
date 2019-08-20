package com.friday.ar.di

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.friday.ar.Theme
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        PreferenceManager.getDefaultSharedPreferences(androidContext())
    } bind SharedPreferences::class
    single {
        Theme(androidContext())
    } bind Theme::class
}