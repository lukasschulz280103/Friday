package com.friday.ar.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity

import com.friday.ar.R

class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
    }

    fun openLibraryPage(v: View) {
        val url = v.tag as String
        val openUrl = Intent()
        openUrl.action = Intent.ACTION_VIEW
        openUrl.data = Uri.parse(url)
        startActivity(openUrl)
    }
}
