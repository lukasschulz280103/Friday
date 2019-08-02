package com.friday.ar.ui.store.packageInstaller

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.friday.ar.R
import com.friday.ar.dialog.store.packageInstaller.PackageInstallerDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class PackageInstallerActivity : AppCompatActivity() {
    companion object {
        private const val RQ_CODE_READ_STORAGE = 8007
        private const val LOGTAG = "PackageInstallerActivity"
    }

    private lateinit var packageInstallerDialog: PackageInstallerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RQ_CODE_READ_STORAGE)
        } else {
            setupDialog()
        }
        setContentView(R.layout.activity_package_installer)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RQ_CODE_READ_STORAGE) {
            if (Arrays.equals(grantResults, intArrayOf(PackageManager.PERMISSION_DENIED))) {
                MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.packageInstaller_perms_denied_accessError)
                        .setMessage(R.string.packageInstaller_perms_denied_accessError_explanation)
                        .setPositiveButton(R.string.retry) { _, _ -> requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RQ_CODE_READ_STORAGE) }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> finishAndRemoveTask() }
                        .create().show()
            } else {
                setupDialog()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun setupDialog() {
        val intent = intent
        val uri = intent.dataString
        Log.d(LOGTAG, uri)
        if (uri != null) {
            var filePath = uri.replace("content://", "")
            filePath = filePath.substring(filePath.indexOf("/"))
            Log.d(LOGTAG, filePath)
            packageInstallerDialog = PackageInstallerDialog.getInstance(filePath)
            packageInstallerDialog.show(supportFragmentManager, "PackageInstallerDialog")
        }
    }

    override fun onBackPressed() {
        if (!packageInstallerDialog.onBackPressed()) super.onBackPressed()
    }

    override fun onPause() {
        finishAndRemoveTask()
        super.onPause()
    }
}
