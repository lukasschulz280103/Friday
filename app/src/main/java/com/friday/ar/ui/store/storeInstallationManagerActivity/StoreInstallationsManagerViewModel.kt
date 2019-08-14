package com.friday.ar.ui.store.storeInstallationManagerActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.friday.ar.FridayApplication
import com.friday.ar.plugin.Plugin
import com.friday.ar.service.plugin.installer.PluginInstaller
import java.io.InputStream

class StoreInstallationsManagerViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        const val LOGTAG = "StoreInstallations"
    }

    private val pluginLoader = FridayApplication.pluginLoader
    val pluginListData = MutableLiveData<List<Plugin>>()

    init {
        pluginLoader.startLoading()
        pluginListData.postValue(pluginLoader.indexedPlugins)
    }

    fun install(inputStream: InputStream) {
        val installer = PluginInstaller(getApplication())
        installer.requestInstallFromInputStream(inputStream)
    }
}