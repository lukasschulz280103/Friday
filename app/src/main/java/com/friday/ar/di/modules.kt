package com.friday.ar.di

import com.friday.ar.account.di.accountModule
import com.friday.ar.auth.di.authModule
import com.friday.ar.wizard.di.wizard

fun moduleList() = listOf(
        appModule,
        wizard,
        authModule,
        accountModule
)