package com.friday.ar.pluginsystem.di

import com.friday.ar.pluginsystem.db.LocalPluginsDB
import org.koin.dsl.module

val pluginSystemModule = module {
    single { LocalPluginsDB.getInstance(get()) }
}