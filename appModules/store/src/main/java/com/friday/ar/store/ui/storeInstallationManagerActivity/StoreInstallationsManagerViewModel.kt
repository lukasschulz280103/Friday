package com.friday.ar.store.ui.storeInstallationManagerActivity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.friday.ar.pluginsystem.db.LocalPluginsDB
import com.friday.ar.pluginsystem.service.installer.PluginInstaller
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.InputStream

class StoreInstallationsManagerViewModel(val context: Context) : ViewModel(), KoinComponent {
    companion object {
        const val LOGTAG = "StoreInstallations"
    }

    private val pluginsDB by inject<LocalPluginsDB>()

    fun install(inputStream: InputStream) {
        val installer = PluginInstaller(context)
        installer.requestInstallFromInputStream(inputStream)
    }

    fun getPluginList() = pluginsDB.indexedPluginsDAO().getCurrentInstalledPlugins()
}