package com.friday.ar.dialog.store.packageInstaller

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.friday.ar.Constant
import com.friday.ar.R
import com.friday.ar.plugin.file.PluginFile
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.util.FileUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import net.lingala.zip4j.exception.ZipException

import org.json.JSONException

import java.io.File
import java.io.IOException

/**
 * This dialog shows up when an external file browser app sends an intent to this app.
 */
class PackageInstallerDialog(chosenFile: File, context: Context) : BottomSheetDialogFragment() {
    private var pluginFile: PluginFile? = null
    private var zippedPluginFile: ZippedPluginFile? = null
    private var exceptionMessage = ""

    init {
        try {
            var fileUrl = chosenFile.path
            for (i in 0..3) {
                fileUrl = fileUrl.substring(fileUrl.indexOf("/"))
            }
            val fileUrlFile = File(fileUrl)
            zippedPluginFile = ZippedPluginFile(fileUrlFile)
            val unzippedPluginFile = Constant.getPluginCacheDir(context, zippedPluginFile!!.file.name.replace(".fpl", "_"))
            zippedPluginFile!!.extractAll(unzippedPluginFile.path)
            val pluginSubDir = File("$unzippedPluginFile/plugin")
            val pluginSubDirTemp = File(unzippedPluginFile.parentFile.path + "/" + unzippedPluginFile.name.replace("_", ""))
            pluginSubDir.renameTo(pluginSubDirTemp)
            FileUtil.deleteDirectory(unzippedPluginFile)
            pluginFile = PluginFile(Constant.getPluginCacheDir(context, zippedPluginFile!!.file.name.replace(".fpl", "")).path)
            FileUtil.deleteDirectory(pluginSubDirTemp)
        } catch (e: ZipException) {
            Log.e(LOGTAG, e.localizedMessage, e)
            exceptionMessage = e.code.toString() + ":" + e.message
        } catch (e: JSONException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        } catch (e: IOException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView: View
        if (!zippedPluginFile!!.isValidZipFile) {
            dialogView = inflater.inflate(R.layout.package_installer_dialog_err, container)
            (dialogView.findViewById<View>(R.id.pluginInstaller_error_description) as TextView).text = exceptionMessage
            dialogView.findViewById<View>(R.id.ok).setOnClickListener { view -> dismiss() }
        } else {
            dialogView = inflater.inflate(R.layout.package_installer_dialog, container)
            val pluginManifest = pluginFile!!.manifest
            val pluginName = dialogView.findViewById<TextView>(R.id.pluginName)
            val authorName = dialogView.findViewById<TextView>(R.id.authorName)
            pluginName.text = pluginManifest.pluginName
            if (pluginManifest.author == null) {
                authorName.text = "Unknown author!"
            } else {
                authorName.text = pluginManifest.author
            }
            dialogView.findViewById<View>(R.id.pluginInstaller_activity_cancel).setOnClickListener { view -> dismiss() }
        }
        return dialogView
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity!!.finish()
    }

    companion object {
        private const val LOGTAG = "PackageInstallerDialog"
    }
}
