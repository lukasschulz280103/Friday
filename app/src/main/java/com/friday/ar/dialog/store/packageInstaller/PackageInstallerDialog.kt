package com.friday.ar.dialog.store.packageInstaller

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.friday.ar.R
import com.friday.ar.databinding.PackageInstallerDialogBinding
import com.friday.ar.plugin.file.Manifest
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.installer.PluginInstaller
import com.friday.ar.plugin.security.VerificationSecurityException
import com.friday.ar.util.cache.PluginFileCacheUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.package_installer_dialog.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.zip.ZipException

/**
 * This dialog shows up when an external file browser app sends an intent to this app.
 */
class PackageInstallerDialog(val chosenFile: File) : BottomSheetDialogFragment() {
    companion object {
        private const val LOGTAG = "PackageInstallerDialog"
        private const val SITE_VIEW_PLUGIN = 0
        private const val SITE_INSTALL_PROGRESS = 1
        private const val SITE_ERROR = 2
    }

    val installProgressMessage = MutableLiveData<String>()

    private lateinit var dialogView: View
    private lateinit var mContext: Context
    private lateinit var viewModel: PackageInstallerDialogViewModel
    private lateinit var cachedPluginFileManifest: Manifest
    private var zippedPluginFile: ZippedPluginFile? = null

    private val onInstallStateChangedListener = object : PluginInstaller.OnInstallationStateChangedListener {
        override fun onProgressChanged(progressMessage: String) {
            installProgressMessage.postValue(progressMessage)
            dialogView.view_animator.displayedChild = SITE_INSTALL_PROGRESS
        }

        override fun onSuccess() {
            dialogView.view_animator.displayedChild = SITE_INSTALL_PROGRESS
        }

        override fun onFailure(e: Exception) {
            dialogView.view_animator.displayedChild = SITE_ERROR
        }

    }


    //TODO:outsource work to the viewmodel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = PackageInstallerDialogBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = this
        dataBinding.progressText = installProgressMessage
        viewModel = PackageInstallerDialogViewModel.Factory(chosenFile, activity!!.application).create(PackageInstallerDialogViewModel::class.java)
        dialogView = dataBinding.root
        dialogView.view_animator.displayedChild = SITE_INSTALL_PROGRESS
        installProgressMessage.postValue(getString(R.string.asset_loader_loading))
        zippedPluginFile = ZippedPluginFile(chosenFile)
        GlobalScope.launch {
            var errorMessage = ""
            try {
                cachedPluginFileManifest = PluginFileCacheUtil.extractManifest(mContext, zippedPluginFile!!)
                activity!!.runOnUiThread {
                    if (!zippedPluginFile!!.isValidZipFile) {
                        dialogView.view_animator.displayedChild = SITE_ERROR
                        dialogView.pluginInstaller_error_description.setText(R.string.pluginInstaller_error_invalid_zip_file)
                    } else {
                        dialogView.pluginName.text = cachedPluginFileManifest.pluginName
                        if (cachedPluginFileManifest.author == null) {
                            dialogView.authorName.setText(R.string.packageInstaller_unknown_author)
                        } else {
                            dialogView.authorName.text = cachedPluginFileManifest.author
                        }
                        dialogView.pluginInstaller_activity_cancel.setOnClickListener { dismiss() }
                        dialogView.startInstallation.setOnClickListener {
                            viewModel.doInstall().addOnInstallationProgressChangedListener(onInstallStateChangedListener)
                            dialogView.view_animator.displayedChild = SITE_INSTALL_PROGRESS
                        }
                        dialogView.view_animator.displayedChild = SITE_VIEW_PLUGIN

                    }
                }
            } catch (e: ZipException) {
                Log.e(LOGTAG, "Error extracting manifest: ${e.message}", e)
                errorMessage = e.message!!
            } catch (e: IOException) {
                Log.e(LOGTAG, "Error extracting manifest: ${e.message}", e)
                errorMessage = e.message!!
            } catch (e: VerificationSecurityException) {
                Log.e(LOGTAG, "Error verifying manifest: ${e.message}", e)
                errorMessage = e.message!!
            } catch (e: IllegalStateException) {
                Log.e(LOGTAG, "Error verifying manifest: ${e.message}", e)
                errorMessage = e.message!!
            } finally {
                if (errorMessage.isNotEmpty()) {
                    activity!!.runOnUiThread {
                        dialogView.view_animator.displayedChild = SITE_ERROR
                        dialogView.pluginInstaller_error_description.text = errorMessage
                    }
                }
            }
        }
        dialogView.errorOk.setOnClickListener { dismiss() }
        return dialogView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity!!.finishAndRemoveTask()
    }

    fun onBackPressed(): Boolean {
        return false
    }
}
