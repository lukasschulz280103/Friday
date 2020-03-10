package com.friday.ar.base.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.friday.ar.base.system.environment.EnvironmentManager
import com.friday.ar.core.util.ar.ArCoreCompatibilityUtil
import com.google.ar.core.Plane
import com.google.ar.sceneform.FrameTime
import org.koin.core.KoinComponent
import org.koin.core.get

class FullscreenActionActivityViewModel(application: Application) : ViewModel(), KoinComponent {

    init {
    }

    companion object {
        const val LOGTAG = "ARActivity"
    }

    private val arCoreCompatibilityUtil = ArCoreCompatibilityUtil(application)
    val isArCoreSupported = arCoreCompatibilityUtil.isArCoreSupported()
    fun checkAvailability() = arCoreCompatibilityUtil.checkAvailability()

    private lateinit var lastDetectedPlanes: Collection<Plane>
    private val arCallback = get<EnvironmentManager.SurfaceFrameCallback>()

    fun onARFrame(frameTime: FrameTime, planes: Collection<Plane>) {
        arCallback.onArFrameUpdated(frameTime)
        if (::lastDetectedPlanes.isInitialized && lastDetectedPlanes != planes) {
            Log.i(LOGTAG, "detected change of currently detected planes: last collection size=${lastDetectedPlanes.size} / new collection size=${planes.size}")
            arCallback.onDetectedPlanesChanged(planes)
        }
        lastDetectedPlanes = planes
    }
}