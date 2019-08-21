package com.friday.ar.base.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.friday.ar.core.util.ar.ArCoreCompatibilityUtil

class FullscreenActionActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val arCoreCompatibilityUtil = ArCoreCompatibilityUtil(application)
    val isArCoreSupported = arCoreCompatibilityUtil.isArCoreSupported
    fun checkAvailability() = arCoreCompatibilityUtil.checkAvailability()
}