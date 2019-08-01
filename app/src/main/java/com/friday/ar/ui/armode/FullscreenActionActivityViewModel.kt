package com.friday.ar.ui.armode

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class FullscreenActionActivityViewModel(application: Application) : AndroidViewModel(application) {
    val isArCoreSupported = MutableLiveData<Boolean>()
    val isArcoreInstalled = MutableLiveData<Boolean>()

    init {
    }
}