package com.friday.ar.ui

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import com.friday.ar.R
import com.friday.ar.Theme
import com.friday.ar.activities.FridayActivity
import com.friday.ar.util.Connectivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_info.*
import java.util.*


@Suppress("UNUSED_PARAMETER")
class InfoActivity : FridayActivity() {
    private lateinit var firebaseVersionDB: FirebaseDatabase
    private lateinit var versionRef: DatabaseReference
    private lateinit var packageInfo: PackageInfo

    internal lateinit var version: TextView

    internal lateinit var versionNumberServer: String
    internal lateinit var versionNumberLocal: String
    private val updatePageRedirect = View.OnClickListener {
        val redirect = Intent()
        redirect.action = Intent.ACTION_VIEW
        redirect.data = Uri.parse("https://github.com/lukasschulz280103/Friday/releases/download/v$versionNumberServer/app-release.apk")
        startActivity(redirect)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Theme.getCurrentAppTheme(this))
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_info)
        version = findViewById(R.id.info_version)
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
            versionNumberLocal = packageInfo.versionName
            version.text = getString(R.string.version_text, versionNumberLocal)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("InfoActivity", e.localizedMessage, e)

        }
        firebaseVersionDB = FirebaseDatabase.getInstance()
        versionRef = firebaseVersionDB.getReference("version")
        if (!Connectivity.isConnected(this@InfoActivity)) {
            info_preference_update_btn.isEnabled = false
            info_preference_update_btn.setText(R.string.network_error_title_short)
        }
        versionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("dataSnapshot", Objects.requireNonNull<Any>(dataSnapshot.value).toString())
                versionNumberServer = dataSnapshot.value as String
                if (versionNumberServer != versionNumberLocal) {
                    info_preference_update_btn.isEnabled = true
                    info_preference_update_btn.text = getString(R.string.info_update_to, versionNumberServer)
                    info_preference_update_btn.setOnClickListener(updatePageRedirect)
                } else {
                    info_preference_update_btn.isEnabled = false
                    info_preference_update_btn.setText(R.string.info_up_to_date)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                version.setText(R.string.check_update_error)
            }
        })
    }

    fun onClickLicense(v: View) {
        val intent = Intent(this, LicenseActivity::class.java)
        startActivity(intent)
    }

    fun onClickContact(v: View) {
        val user = FirebaseAuth.getInstance().currentUser
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "friday.enterprises.ar@gmail.com", null))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support request" + if (user != null) "for #" + user.uid else "")
        startActivity(Intent.createChooser(intent, getString(R.string.contact)))
    }
}
