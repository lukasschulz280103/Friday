package com.friday.ar.wizard.fragments.deviceCompatibility

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.friday.ar.core.util.ar.ArCoreCompatibilityUtil

class DeviceCompatibilityFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val arCoreCompatibilityUtil = ArCoreCompatibilityUtil(application)
    val isArCoreSupported = arCoreCompatibilityUtil.isArCoreSupported
    fun checkAvailability() = arCoreCompatibilityUtil.checkAvailability()
}