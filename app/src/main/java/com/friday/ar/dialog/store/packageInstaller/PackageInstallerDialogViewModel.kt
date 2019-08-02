package com.friday.ar.dialog.store.packageInstaller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.friday.ar.plugin.file.Manifest
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.installer.PluginInstaller
import com.friday.ar.util.cache.PluginFileCacheUtil
import java.io.File

class PackageInstallerDialogViewModel(chosenFile: File, application: Application) : AndroidViewModel(application) {
    private var zippedPluginFile: ZippedPluginFile
    val manifest = MutableLiveData<Manifest>()

    init {
        var fileUrl = chosenFile.path
        for (i in 0..3) {
            fileUrl = fileUrl.substring(fileUrl.indexOf("/"))
        }
        val fileUrlFile = File(fileUrl)
        zippedPluginFile = ZippedPluginFile(fileUrlFile)
        manifest.postValue(PluginFileCacheUtil.extractManifest(application, ZippedPluginFile(chosenFile)))
    }

    val onInstallProgressChangedListener = object : PluginInstaller.OnInstallationStateChangedListener {
        override fun onProgressChanged(progressMessage: String) {
        }

        override fun onSuccess() {

        }

        override fun onFailure(e: Exception) {

        }

    }

    fun doInstall(): PluginInstaller {
        val installer = PluginInstaller(getApplication())
        installer.addOnInstallationProgressChangedListener(onInstallProgressChangedListener)
        installer.installFrom(zippedPluginFile)
        return installer
    }

    open class Factory(val chosenFile: File, val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PackageInstallerDialogViewModel(chosenFile, application) as T
        }
    }
}