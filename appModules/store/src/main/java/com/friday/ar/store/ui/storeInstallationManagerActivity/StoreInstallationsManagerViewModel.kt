package com.friday.ar.store.ui.storeInstallationManagerActivity

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.service.PluginLoader
import com.friday.ar.pluginsystem.service.installer.PluginInstaller
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.InputStream

class StoreInstallationsManagerViewModel(val context: Context) : ViewModel(), KoinComponent {
    companion object {
        const val LOGTAG = "StoreInstallations"
    }

    val pluginListData = MutableLiveData<List<Plugin>>()

    init {
        val pluginLoader = get<PluginLoader>()
        Log.d(LOGTAG, "indexed installed plugins: ${pluginLoader.indexedPlugins}")

        pluginListData.postValue(pluginLoader.indexedPlugins)
    }



    fun install(inputStream: InputStream) {
        val installer = PluginInstaller(context)
        installer.requestInstallFromInputStream(inputStream)
    }
}