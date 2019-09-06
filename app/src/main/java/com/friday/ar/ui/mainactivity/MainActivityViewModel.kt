package com.friday.ar.ui.mainactivity

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.content.pm.PackageManager.NameNotFoundException
import android.os.PowerManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.core.Constant
import com.friday.ar.dashboard.internal.base.BaseDashboardListItem
import com.friday.ar.pluginsystem.Plugin
import org.koin.core.KoinComponent
import org.koin.core.get


class MainActivityViewModel(context: Context) : ViewModel(), KoinComponent {
    private val energySaverActive = MutableLiveData<Boolean>()
    private val isUpdatedVersion = MutableLiveData<Boolean>()

    private val isOldVersionInstalled = MutableLiveData<Pair<Boolean, String?>>()
    private val isFirstUse = MutableLiveData<Boolean>()
    private val dashboardListData = MutableLiveData<ArrayList<BaseDashboardListItem>>()
    fun getEnergySaverState() = energySaverActive as LiveData<Boolean>
    fun getIsUpdatedVersion() = isUpdatedVersion as LiveData<Boolean>
    val dataList = ArrayList<BaseDashboardListItem>()


    /**
     * @return liveData containing a pair. [Pair]<[Boolean] if old app is installed, [String] packageName of old app that is installed>
     */
    fun getOldVersionInstalledState() = isOldVersionInstalled as LiveData<Pair<Boolean, String?>>

    fun getFirstUseState() = isFirstUse as LiveData<Boolean>
    fun getDashBoardListData() = dashboardListData as LiveData<ArrayList<BaseDashboardListItem>>

    val defaultSharedPreferences: SharedPreferences = get()

    init {

        val packageManager = context.packageManager
        try {
            val pkgInf = packageManager.getPackageInfo(context.packageName, 0)
            if (defaultSharedPreferences.getString("version", "0") != pkgInf.versionName || defaultSharedPreferences.getBoolean("pref_devmode_show_changelog", false)) {
                isUpdatedVersion.postValue(true)
                val editor = defaultSharedPreferences.edit()
                editor.putString("version", pkgInf.versionName)
                editor.apply()
            }
        } catch (e: NameNotFoundException) {
            isUpdatedVersion.postValue(false)
        }


        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        energySaverActive.postValue(pm.isPowerSaveMode)

        lateinit var currentAppPackageName: String
        try {
            currentAppPackageName = Constant.OldPacakgeNames.RBPIECLIENT
            packageManager.getPackageInfo(currentAppPackageName, GET_ACTIVITIES)
            isOldVersionInstalled.postValue(Pair(true, currentAppPackageName))
        } catch (e: NameNotFoundException) {
            isOldVersionInstalled.postValue(Pair(false, null))
        }
        try {
            currentAppPackageName = Constant.OldPacakgeNames.HEAD_DISPLAY_CLIENT
            packageManager.getPackageInfo(currentAppPackageName, GET_ACTIVITIES)
            isOldVersionInstalled.postValue(Pair(true, currentAppPackageName))
        } catch (e: NameNotFoundException) {
            isOldVersionInstalled.postValue(Pair(false, null))
        }

        val examplePlugin = Plugin()
        examplePlugin.name = "TestPlugin"
        for (i in 1..50) {
            dataList.add(BaseDashboardListItem.getInstance(examplePlugin))
        }
        dataList.add(BaseDashboardListItem.getInstance(examplePlugin))
        dashboardListData.postValue(dataList)
    }

    fun runRefresh() {
        if (dataList.isNotEmpty()) dataList.remove(dataList[0])
        dashboardListData.postValue(dataList)
    }

    fun checkForFirstUse() {
        if (defaultSharedPreferences.getBoolean("isFirstUse", true)) {
            isFirstUse.postValue(true)
        } else isFirstUse.postValue(false)
    }
}