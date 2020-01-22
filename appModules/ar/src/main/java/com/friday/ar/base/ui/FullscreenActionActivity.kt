package com.friday.ar.base.ui

import android.content.Intent
import android.util.Log
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.friday.ar.base.R
import com.friday.ar.core.Constant
import com.friday.ar.core.activity.FridayActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import extensioneer.notNull
import org.koin.android.viewmodel.ext.android.viewModel


class FullscreenActionActivity : FridayActivity() {
    companion object {
        const val LOGTAG = "ARMode"
    }

    private lateinit var mArCoreSession: Session
    private val viewModel by viewModel<FullscreenActionActivityViewModel>()

    private var arFragment: ArFragment? = null

    override fun onStart() {
        super.onStart()
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

                    onReady()
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
        viewModel.checkAvailability()
    }

    private fun onReady() {
        arFragment.notNull {
            arSceneView.scene.addOnUpdateListener { frameTime ->
                Log.d(LOGTAG, "onUpdate")
                val planeUpdateIntent = Intent(Constant.AR.SystemBroadcast.AR_SCENE_UPDATED)
                arSceneView.arFrame.notNull { viewModel.onARFrame(frameTime, getUpdatedTrackables(Plane::class.java)) }
                LocalBroadcastManager.getInstance(this@FullscreenActionActivity).sendBroadcast(planeUpdateIntent)
            }
        }
    }
}