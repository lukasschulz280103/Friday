package com.friday.ar.store.ui.storeInstallationManagerActivity

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.service.installer.PluginInstaller
import java.io.InputStream

class StoreInstallationsManagerViewModel(val context: Context) : ViewModel() {
    companion object {
        const val LOGTAG = "StoreInstallations"
    }

    val pluginListData = MutableLiveData<List<Plugin>>()


    fun install(inputStream: InputStream) {
        val installer = PluginInstaller(context)
        installer.requestInstallFromInputStream(inputStream)
    }
}