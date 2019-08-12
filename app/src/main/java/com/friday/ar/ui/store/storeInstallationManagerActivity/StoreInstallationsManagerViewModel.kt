package com.friday.ar.ui.store.storeInstallationManagerActivity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.friday.ar.Constant
import com.friday.ar.FridayApplication
import com.friday.ar.extensionMethods.toFile
import com.friday.ar.plugin.Plugin
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.service.plugin.installer.PluginInstaller
import net.lingala.zip4j.exception.ZipException
import java.io.IOException
import java.io.InputStream
import java.util.*

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

    fun requestInstallFromInputStream(inputStream: InputStream) {
        val application = getApplication<FridayApplication>()
        val installer = PluginInstaller(getApplication())
        installer.isSilent = false
        try {
            val cacheFile = inputStream.toFile(Constant.getPluginCacheDir(application).path + "/" + UUID.randomUUID().toString() + ".fpl")
            installer.installFrom(ZippedPluginFile(cacheFile))
            cacheFile.delete()
        } catch (e: IOException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        } catch (e: ZipException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        }
    }
}