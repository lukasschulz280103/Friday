package com.friday.ar.store.dialog

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.pluginsystem.cache.PluginFileCacheUtil
import com.friday.ar.pluginsystem.file.Manifest
import com.friday.ar.pluginsystem.file.ZippedPluginFile
import com.friday.ar.pluginsystem.security.VerificationSecurityException
import com.friday.ar.pluginsystem.service.installer.PluginInstaller
import com.friday.ar.store.R
import java.io.File
import java.io.IOException
import java.util.zip.ZipException

class PackageInstallerDialogViewModel(val context: Context) : ViewModel() {
    val manifest = MutableLiveData<Manifest>()
    private lateinit var zippedPluginFile: ZippedPluginFile
    val installProgressMessage = MutableLiveData<String>()
    val displayedSite = MutableLiveData<Int>()
    val errorMessage = MutableLiveData<String>()
    val progressIcon = MutableLiveData<@androidx.annotation.DrawableRes Int>()
    val isProgressShown = MutableLiveData<Boolean>()

    fun init(chosenFile: File) {
        zippedPluginFile = ZippedPluginFile(chosenFile)
        installProgressMessage.postValue(context.getString(R.string.loading))
        try {
            val cachedPluginFileManifest = PluginFileCacheUtil.extractManifest(context, zippedPluginFile)
            if (!zippedPluginFile.isValidZipFile) {
                displayedSite.postValue(PackageInstallerDialog.SITE_ERROR)
                errorMessage.postValue(context.getString(R.string.pluginInstaller_error_invalid_zip_file))
            } else {
                errorMessage.postValue(context.getString(R.string.pluginInstaller_error_invalid_zip_file))
                if (cachedPluginFileManifest.author == null) {
                    cachedPluginFileManifest.author = context.getString(R.string.packageInstaller_unknown_author)
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
            manifest.postValue(PluginFileCacheUtil.extractManifest(context, ZippedPluginFile(chosenFile)))
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
            installProgressMessage.postValue(context.getString(R.string.pluginInstaller_success_ticker_text, manifest.value!!.pluginName))
        }

        override fun onFailure(e: Exception) {
            isProgressShown.postValue(false)
            progressIcon.postValue(R.drawable.ic_twotone_error_outline_24px)
            displayedSite.postValue(PackageInstallerDialog.SITE_ERROR)
            installProgressMessage.postValue(context.getString(R.string.pluginInstaller_error_installation_failed))
        }

    }

    fun doInstall(): PluginInstaller {
        val installer = PluginInstaller(context)
        displayedSite.postValue(PackageInstallerDialog.SITE_INSTALL_PROGRESS)
        isProgressShown.postValue(true)
        installer.addOnInstallationProgressChangedListener(onInstallProgressChangedListener)
        installer.installFrom(zippedPluginFile)
        return installer
    }

}