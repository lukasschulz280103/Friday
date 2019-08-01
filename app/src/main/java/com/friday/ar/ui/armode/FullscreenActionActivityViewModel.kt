package com.friday.ar.ui.armode

import android.app.Application
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.ar.core.ArCoreApk

class FullscreenActionActivityViewModel(application: Application) : AndroidViewModel(application) {
    val isArCoreSupported = MutableLiveData<ArCoreApk.Availability>()

    fun checkAvailability() {
        val apk = ArCoreApk.getInstance()
        when (val availability = apk.checkAvailability(getApplication())) {
            ArCoreApk.Availability.UNKNOWN_ERROR -> {
                isArCoreSupported.postValue(availability)
            }
            ArCoreApk.Availability.UNKNOWN_CHECKING -> {
                Handler().postDelayed({
                    checkAvailability()
                }, 200)
            }
            ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> {
                isArCoreSupported.postValue(availability)
            }
            ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                isArCoreSupported.postValue(availability)
            }
            ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                isArCoreSupported.postValue(availability)
            }
            ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> {
                isArCoreSupported.postValue(availability)
            }
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                isArCoreSupported.postValue(availability)
            }
            else -> {
                isArCoreSupported.postValue(ArCoreApk.Availability.UNKNOWN_ERROR)
            }
        }
    }
}