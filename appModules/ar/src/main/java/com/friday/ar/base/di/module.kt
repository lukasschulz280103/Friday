package com.friday.ar.base.di

import com.friday.ar.base.system.environment.EnvironmentManager
import com.friday.ar.base.ui.FullscreenActionActivityViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val arModule = module {
    viewModel { FullscreenActionActivityViewModel(get()) }
    single { EnvironmentManager.SurfaceFrameCallback() }
}