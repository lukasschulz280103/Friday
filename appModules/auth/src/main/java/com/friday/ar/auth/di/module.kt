package com.friday.ar.auth.di

import com.friday.ar.auth.fragments.SignInFragmentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { SignInFragmentViewModel(get()) }
}