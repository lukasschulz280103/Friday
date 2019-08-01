package com.friday.ar.ui.armode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.friday.ar.Constant
import com.friday.ar.R
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.sceneform.ux.ArFragment


class FullscreenActionActivity : AppCompatActivity() {
    companion object {
        const val LOGTAG = "ARMode"
    }

    private lateinit var mArCoreSession: Session
    private var mUserRequestedInstall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var arFragment: ArFragment? = null

        val returnOnErrorIntent = Intent()
        try {
            val apk = ArCoreApk.getInstance()
            when (apk.requestInstall(this, mUserRequestedInstall)) {
                ArCoreApk.InstallStatus.INSTALLED -> {
                    mArCoreSession = Session(this@FullscreenActionActivity)
                }
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    mUserRequestedInstall = false
                    finish()
                    return
                }
            }
            setContentView(R.layout.activity_fullscreen_action)
            arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

            mArCoreSession = Session(this)

            val config = mArCoreSession.config
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            config.focusMode = Config.FocusMode.AUTO
            mArCoreSession.configure(config)
            arFragment.arSceneView.setupSession(mArCoreSession)
        } catch (e: Exception) {
            Log.e(LOGTAG, "Could not create AR Session: ${e.message}", e)
            when (e) {
                is UnavailableArcoreNotInstalledException -> returnOnErrorIntent.putExtra("errtpe", Constant.ArCoreSession.Error.NOT_INSTALLED)
                is UnavailableApkTooOldException -> returnOnErrorIntent.putExtra("errtype", Constant.ArCoreSession.Error.OLD_APK)
                is UnavailableSdkTooOldException -> returnOnErrorIntent.putExtra("errtype", Constant.ArCoreSession.Error.OLD_SDK_TOOL)
                is UnavailableDeviceNotCompatibleException -> returnOnErrorIntent.putExtra("errtype", Constant.ArCoreSession.Error.DEVICE_INCOMPATIBLE)
            }
            setResult(Activity.RESULT_CANCELED, returnOnErrorIntent)
            finish()
            return
        }
    }
}