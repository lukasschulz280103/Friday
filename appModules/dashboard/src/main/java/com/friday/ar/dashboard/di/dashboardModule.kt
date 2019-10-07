package com.friday.ar.dashboard.di

import com.friday.ar.dashboard.ui.dashboardFragment.DashboardFragmentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dashboardModule = module {
    viewModel { DashboardFragmentViewModel() }
}