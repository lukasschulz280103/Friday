package com.friday.ar.base.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import com.friday.ar.base.system.environment.EnvironmentManager
import com.friday.ar.core.util.ar.ArCoreCompatibilityUtil
import com.google.ar.sceneform.FrameTime
import org.koin.core.KoinComponent
import org.koin.core.get

class FullscreenActionActivityViewModel(application: Application) : ViewModel(), KoinComponent {
    private val arCoreCompatibilityUtil = ArCoreCompatibilityUtil(application)
    val isArCoreSupported = arCoreCompatibilityUtil.isArCoreSupported()
    fun checkAvailability() = arCoreCompatibilityUtil.checkAvailability()

    fun onARFrame(frameTime: FrameTime) {
        val arCallback = get<EnvironmentManager.SurfaceFrameCallback>()
        arCallback.onArFrameUpdated(frameTime)


    }
}