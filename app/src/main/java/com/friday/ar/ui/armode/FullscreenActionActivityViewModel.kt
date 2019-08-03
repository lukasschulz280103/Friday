package com.friday.ar.ui.armode

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.friday.ar.util.ArCoreCompatibilityUtil

class FullscreenActionActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val arCoreCompatibilityUtil = ArCoreCompatibilityUtil(application)
    val isArCoreSupported = arCoreCompatibilityUtil.isArCoreSupported
    fun checkAvailability() = arCoreCompatibilityUtil.checkAvailability()
}