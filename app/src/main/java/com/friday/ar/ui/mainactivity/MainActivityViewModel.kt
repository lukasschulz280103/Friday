package com.friday.ar.ui.mainactivity

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.PowerManager
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val energySaverActive = MutableLiveData<Boolean>()
    val isUpdatedVersion = MutableLiveData<Boolean>()
    val isOldVersionInstalled = MutableLiveData<Boolean>()
    val isFirstUse = MutableLiveData<Boolean>()

    init {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
        val packageManager = application.packageManager
        try {
            val pkgInf = packageManager.getPackageInfo(application.packageName, 0)
            if (defaultSharedPreferences.getString("version", "0") != pkgInf.versionName || defaultSharedPreferences.getBoolean("pref_devmode_show_changelog", false)) {
                isUpdatedVersion.postValue(true)
                val editor = defaultSharedPreferences.edit()
                editor.putString("version", pkgInf.versionName)
                editor.apply()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            isUpdatedVersion.postValue(false)
        }


        val pm = application.getSystemService(Context.POWER_SERVICE) as PowerManager
        energySaverActive.postValue(pm.isPowerSaveMode)

        try {
            packageManager.getPackageInfo("com.code_design_camp.client.rasberrypie.rbpieclient", PackageManager.GET_ACTIVITIES)
            isOldVersionInstalled.postValue(true)
        } catch (e: PackageManager.NameNotFoundException) {
            isOldVersionInstalled.postValue(false)
        }


        if (defaultSharedPreferences.getBoolean("isFirstUse", true)) {
            isFirstUse.postValue(true)
            defaultSharedPreferences.edit().putBoolean("isFirstUse", false).apply()
        } else isFirstUse.postValue(false)
    }
}