package com.friday.ar.ui.mainactivity

import android.content.Context
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.content.pm.PackageManager.NameNotFoundException
import android.os.PowerManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.friday.ar.R
import com.friday.ar.core.Constant
import com.friday.ar.dashboard.DashboardListItem


class MainActivityViewModel(context: Context) : ViewModel() {
    private val energySaverActive = MutableLiveData<Boolean>()
    private val isUpdatedVersion = MutableLiveData<Boolean>()

    private val isOldVersionInstalled = MutableLiveData<Pair<Boolean, String?>>()
    private val isFirstUse = MutableLiveData<Boolean>()
    private val dashboardListData = MutableLiveData<List<DashboardListItem>>()
    fun getEnergySaverState() = energySaverActive as LiveData<Boolean>
    fun getIsUpdatedVersion() = isUpdatedVersion as LiveData<Boolean>

    /**
     * @return liveData containing a pair. [Pair]<[Boolean] if old app is installed, [String] packageName of old app that is installed>
     */
    fun getOldVersionInstalledState() = isOldVersionInstalled as LiveData<Pair<Boolean, String?>>

    fun getFirstUseState() = isFirstUse as LiveData<Boolean>
    fun getDashBoardListData() = dashboardListData as LiveData<List<DashboardListItem>>

    init {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


        if (defaultSharedPreferences.getInt("theme", 0) == 0) {
            defaultSharedPreferences.edit().putInt("theme", R.style.AppTheme).apply()
        }

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


        if (defaultSharedPreferences.getBoolean("isFirstUse", true)) {
            isFirstUse.postValue(true)
        } else isFirstUse.postValue(false)


        val dataList = ArrayList<DashboardListItem>()
        dashboardListData.postValue(dataList)
    }

    fun runRefresh() {
        dashboardListData.postValue(ArrayList(0))
    }
}