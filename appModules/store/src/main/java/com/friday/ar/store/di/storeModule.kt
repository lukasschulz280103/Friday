package com.friday.ar.store.di

import com.friday.ar.store.dialog.PackageInstallerDialogViewModel
import com.friday.ar.store.ui.storeInstallationManagerActivity.StoreInstallationsManagerViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val storeModule = module {
    viewModel { StoreInstallationsManagerViewModel(get()) }
    viewModel { PackageInstallerDialogViewModel(get()) }
}