package com.friday.ar.ui.store.packageInstaller

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.friday.ar.R
import com.friday.ar.dialog.store.packageInstaller.PackageInstallerDialog
import java.io.File

class PackageInstallerActivity : AppCompatActivity() {
    companion object {
        private const val LOGTAG = "PackageInstallerActivity"
    }

    private lateinit var packageInstallerDialog: PackageInstallerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_installer)
        val intent = intent
        val uri = intent.dataString
        Log.d(LOGTAG, uri)
        if (uri != null) {
            var filePath = uri.replace("content://", "")
            filePath = "file://" + filePath.substring(filePath.indexOf("/"))
            packageInstallerDialog = PackageInstallerDialog(File(filePath))
            packageInstallerDialog.show(supportFragmentManager, "PackageInstallerDialog")
        }
    }

    override fun onBackPressed() {
        if (!packageInstallerDialog.onBackPressed()) super.onBackPressed()
    }
}
