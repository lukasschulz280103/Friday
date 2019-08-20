package com.friday.ar.account.di

import com.friday.ar.account.data.UserStore
import org.koin.dsl.module

val accountModule = module {
    single { UserStore(get()) }
}