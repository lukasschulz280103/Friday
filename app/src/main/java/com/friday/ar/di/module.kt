package com.friday.ar.di

import com.friday.ar.core.Theme
import com.friday.ar.fragments.dialogFragments.changelog.ChangeLogDialogViewModel
import com.friday.ar.ui.mainactivity.MainActivityViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Theme(get()) }
    viewModel { ChangeLogDialogViewModel(get()) }
    viewModel { MainActivityViewModel(get()) }
}