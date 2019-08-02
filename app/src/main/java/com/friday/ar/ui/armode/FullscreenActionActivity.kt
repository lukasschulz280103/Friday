package com.friday.ar.ui.armode

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.friday.ar.R
import com.friday.ar.extensionMethods.notNull
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment


class FullscreenActionActivity : AppCompatActivity() {
    companion object {
        const val LOGTAG = "ARMode"
    }

    private lateinit var mArCoreSession: Session
    private lateinit var viewModel: FullscreenActionActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(application)).get(FullscreenActionActivityViewModel::class.java)

        var arFragment: ArFragment?
        viewModel.isArCoreSupported.observe(this, Observer { arCoreAvailability ->
            Log.d(LOGTAG, "observed change. availability: $arCoreAvailability")
            when (arCoreAvailability) {
                ArCoreApk.Availability.UNKNOWN_ERROR -> {
                    Log.d(LOGTAG, "could not start ARMode. code $arCoreAvailability")
                    intent.putExtra("errtype", arCoreAvailability.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
                ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> {
                    Log.d(LOGTAG, "could not start ARMode. code $arCoreAvailability")
                    intent.putExtra("errtype", arCoreAvailability.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
                ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                    Log.d(LOGTAG, "could not start ARMode. code $arCoreAvailability")
                    intent.putExtra("errtype", arCoreAvailability.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                    Log.d(LOGTAG, "could not start ARMode. code $arCoreAvailability")
                    intent.putExtra("errtype", arCoreAvailability.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> {
                    Log.d(LOGTAG, "could not start ARMode. code $arCoreAvailability")
                    intent.putExtra("errtype", arCoreAvailability.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                    setContentView(R.layout.activity_fullscreen_action)
                    arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment
                    mArCoreSession = Session(this)

                    val config = mArCoreSession.config
                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    config.focusMode = Config.FocusMode.AUTO
                    mArCoreSession.configure(config)
                    arFragment.notNull { arSceneView.setupSession(mArCoreSession) }
                }
                ArCoreApk.Availability.UNKNOWN_CHECKING -> {
                }
                else -> {
                    Log.d(LOGTAG, "could not start ARMode. code $arCoreAvailability")
                    intent.putExtra("errtype", ArCoreApk.Availability.UNKNOWN_ERROR)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkAvailability()
    }
}