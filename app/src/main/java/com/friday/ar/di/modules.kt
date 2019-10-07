package com.friday.ar.di

import com.friday.ar.account.di.accountModule
import com.friday.ar.auth.di.authModule
import com.friday.ar.base.di.arModule
import com.friday.ar.core.di.coreModule
import com.friday.ar.core_ui.di.core_uiModule
import com.friday.ar.dashboard.di.dashboardModule
import com.friday.ar.feedback.di.feedbackModule
import com.friday.ar.pluginsystem.di.pluginSystemModule
import com.friday.ar.preferences.ui.di.preferenceModule
import com.friday.ar.store.di.storeModule
import com.friday.ar.wizard.di.wizard

fun moduleList() = listOf(
        coreModule,
        core_uiModule,
        appModule,
        wizard,
        authModule,
        accountModule,
        arModule,
        storeModule,
        pluginSystemModule,
        feedbackModule,
        preferenceModule,
        dashboardModule
)