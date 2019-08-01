package com.friday.ar.ui.mainactivity

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.content.pm.PackageManager.NameNotFoundException
import android.os.PowerManager
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.friday.ar.dashboard.DashboardListItem
import com.google.ar.core.ArCoreApk


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val energySaverActive = MutableLiveData<Boolean>()
    val isUpdatedVersion = MutableLiveData<Boolean>()
    val isOldVersionInstalled = MutableLiveData<Pair<Boolean, String?>>()
    val isFirstUse = MutableLiveData<Boolean>()
    val dashboardListData = MutableLiveData<ArrayList<DashboardListItem>>()
    val arCoreApkAvailability = MutableLiveData<ArCoreApk.Availability>()

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
        } catch (e: NameNotFoundException) {
            isUpdatedVersion.postValue(false)
        }


        val pm = application.getSystemService(Context.POWER_SERVICE) as PowerManager
        energySaverActive.postValue(pm.isPowerSaveMode)

        var currentAppPackageName = ""
        try {
            currentAppPackageName = "com.code_design_camp.client.rasberrypie.rbpieclient"
            packageManager.getPackageInfo(currentAppPackageName, GET_ACTIVITIES)
            isOldVersionInstalled.postValue(Pair(true, currentAppPackageName))
        } catch (e: NameNotFoundException) {
            isOldVersionInstalled.postValue(Pair(false, null))
        }
        try {
            currentAppPackageName = "com.code_design_camp.client.friday.HeadDisplayClient"
            packageManager.getPackageInfo(currentAppPackageName, GET_ACTIVITIES)
            isOldVersionInstalled.postValue(Pair(true, currentAppPackageName))
        } catch (e: NameNotFoundException) {
            isOldVersionInstalled.postValue(Pair(false, null))
        }


        if (defaultSharedPreferences.getBoolean("isFirstUse", true)) {
            isFirstUse.postValue(true)
            defaultSharedPreferences.edit().putBoolean("isFirstUse", false).apply()
        } else isFirstUse.postValue(false)


        val dataList = ArrayList<DashboardListItem>()
        dashboardListData.postValue(dataList)
    }

    fun runRefresh() {
        dashboardListData.postValue(ArrayList(0))
    }
}