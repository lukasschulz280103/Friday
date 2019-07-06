package com.friday.ar.ui.store.packageInstaller

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.friday.ar.R
import com.friday.ar.dialog.store.packageInstaller.PackageInstallerDialog
import java.io.File

class PackageInstallerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_installer)
        val intent = intent
        val uri = intent.dataString
        Log.d(LOGTAG, uri)
        if (uri != null) {
            var filePath = uri.replace("content://", "")
            filePath = "file://" + filePath.substring(filePath.indexOf("/"))
            val packageInstallerDialog = PackageInstallerDialog(File(filePath), this)
            packageInstallerDialog.show(supportFragmentManager, "PackageInstallerDialog")
        }
    }

    companion object {

        private val LOGTAG = "PackageInstallerActivity"
    }
}
