package com.friday.ar.store.ui.storeInstallationManagerActivity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.friday.ar.pluginsystem.service.installer.PluginInstaller
import org.koin.core.KoinComponent
import java.io.InputStream

class StoreInstallationsManagerViewModel(val context: Context) : ViewModel(), KoinComponent {
    companion object {
        const val LOGTAG = "StoreInstallations"
    }




    fun install(inputStream: InputStream) {
        val installer = PluginInstaller(context)
        installer.requestInstallFromInputStream(inputStream)
    }
}