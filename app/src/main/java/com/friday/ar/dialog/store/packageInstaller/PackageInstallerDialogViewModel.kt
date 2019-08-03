package com.friday.ar.dialog.store.packageInstaller

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.friday.ar.R
import com.friday.ar.plugin.file.Manifest
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.installer.PluginInstaller
import com.friday.ar.plugin.security.VerificationSecurityException
import com.friday.ar.util.cache.PluginFileCacheUtil
import java.io.File
import java.io.IOException
import java.util.zip.ZipException

class PackageInstallerDialogViewModel(chosenFile: File, application: Application) : AndroidViewModel(application) {
    val manifest = MutableLiveData<Manifest>()
    private var zippedPluginFile = ZippedPluginFile(chosenFile)
    val installProgressMessage = MutableLiveData<String>()
    val displayedSite = MutableLiveData<Int>()
    val errorMessage = MutableLiveData<String>()
    val progressIcon = MutableLiveData<@androidx.annotation.DrawableRes Int>()
    val isProgressShown = MutableLiveData<Boolean>()

    init {
        installProgressMessage.postValue(application.getString(R.string.asset_loader_loading))
        try {
            val cachedPluginFileManifest = PluginFileCacheUtil.extractManifest(application, zippedPluginFile)
            if (!zippedPluginFile.isValidZipFile) {
                displayedSite.postValue(PackageInstallerDialog.SITE_ERROR)
                errorMessage.postValue(application.getString(R.string.pluginInstaller_error_invalid_zip_file))
            } else {
                errorMessage.postValue(application.getString(R.string.pluginInstaller_error_invalid_zip_file))
                if (cachedPluginFileManifest.author == null) {
                    cachedPluginFileManifest.author = application.getString(R.string.packageInstaller_unknown_author)
                }
                displayedSite.postValue(PackageInstallerDialog.SITE_VIEW_PLUGIN)

            }
        } catch (e: ZipException) {
            Log.e(PackageInstallerDialog.LOGTAG, "Error extracting manifest: ${e.message}", e)
            errorMessage.postValue(e.message)
        } catch (e: IOException) {
            Log.e(PackageInstallerDialog.LOGTAG, "Error extracting manifest: ${e.message}", e)
            errorMessage.postValue(e.message)
        } catch (e: VerificationSecurityException) {
            Log.e(PackageInstallerDialog.LOGTAG, "Error verifying manifest: ${e.message}", e)
            errorMessage.postValue(e.message)
        } catch (e: IllegalStateException) {
            Log.e(PackageInstallerDialog.LOGTAG, "Error verifying manifest: ${e.message}", e)
            errorMessage.postValue(e.message)
        } finally {
            if (errorMessage.value != null && errorMessage.value!!.isNotEmpty()) {
                displayedSite.postValue(PackageInstallerDialog.SITE_ERROR)
            }
        }
        var fileUrl = chosenFile.path
        for (i in 0..3) {
            fileUrl = fileUrl.substring(fileUrl.indexOf("/"))
        }
        val fileUrlFile = File(fileUrl)
        zippedPluginFile = ZippedPluginFile(fileUrlFile)
        try {
            manifest.postValue(PluginFileCacheUtil.extractManifest(application, ZippedPluginFile(chosenFile)))
        } catch (e: java.lang.Exception) {
            errorMessage.postValue(e.message)
            displayedSite.postValue(PackageInstallerDialog.SITE_ERROR)
        }
    }

    private val onInstallProgressChangedListener = object : PluginInstaller.OnInstallationStateChangedListener {
        override fun onProgressChanged(progressMessage: String) {
            displayedSite.postValue(PackageInstallerDialog.SITE_INSTALL_PROGRESS)
            installProgressMessage.postValue(progressMessage)
        }

        override fun onSuccess() {
            isProgressShown.postValue(false)
            progressIcon.postValue(R.drawable.ic_check_green_24dp)
            installProgressMessage.postValue(application.getString(R.string.pluginInstaller_success_ticker_text, manifest.value!!.pluginName))
        }

        override fun onFailure(e: Exception) {
            isProgressShown.postValue(false)
            progressIcon.postValue(R.drawable.ic_twotone_error_outline_24px)
            displayedSite.postValue(PackageInstallerDialog.SITE_ERROR)
            installProgressMessage.postValue(application.getString(R.string.pluginInstaller_error_installation_failed))
        }

    }

    fun doInstall(): PluginInstaller {
        val installer = PluginInstaller(getApplication())
        displayedSite.postValue(PackageInstallerDialog.SITE_INSTALL_PROGRESS)
        isProgressShown.postValue(true)
        installer.addOnInstallationProgressChangedListener(onInstallProgressChangedListener)
        installer.installFrom(zippedPluginFile)
        return installer
    }

    open class Factory(private val chosenFile: File, val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PackageInstallerDialogViewModel(chosenFile, application) as T
        }
    }

}