package com.friday.ar.di

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.friday.ar.Theme
import com.friday.ar.fragments.dialogFragments.changelog.ChangeLogDialogViewModel
import com.friday.ar.ui.store.storeInstallationManagerActivity.StoreInstallationsManagerViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        PreferenceManager.getDefaultSharedPreferences(get())
    }
    single {
        Theme(get())
    }
    viewModel { ChangeLogDialogViewModel(get()) }
    viewModel { StoreInstallationsManagerViewModel(get()) }
}