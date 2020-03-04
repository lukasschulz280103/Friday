package com.friday.ar.ui.mainactivity

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.content.pm.PackageManager.NameNotFoundException
import android.nfc.NfcAdapter
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.core.BuildConfig
import com.friday.ar.core.Constant
import org.koin.core.KoinComponent
import org.koin.core.get


class MainActivityViewModel(val context: Context) : ViewModel(), KoinComponent {
    private val energySaverActive = MutableLiveData<Boolean>()
    private val isUpdatedVersion = MutableLiveData<Boolean>()

    private val isOldVersionInstalled = MutableLiveData<Pair<Boolean, String?>>()
    private val isFirstUse = MutableLiveData<Boolean>()

    fun getEnergySaverState() = energySaverActive as LiveData<Boolean>
    fun getIsUpdatedVersion() = isUpdatedVersion as LiveData<Boolean>


    /**
     * @return liveData containing a pair. [Pair]<[Boolean] if old app is installed, [String] packageName of old app that is installed>
     */
    fun getOldVersionInstalledState() = isOldVersionInstalled as LiveData<Pair<Boolean, String?>>

    fun getFirstUseState() = isFirstUse as LiveData<Boolean>

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
    }


    fun checkForFirstUse() {
        //checking for first use to show wizard if user has done a fresh install
        if (defaultSharedPreferences.getBoolean("isFirstUse", true)) {
            isFirstUse.postValue(true)
        } else isFirstUse.postValue(false)
    }

    enum class NFCState {
        NFC_DISABLED,
        NFC_ENABLED,
        NFC_NOT_AVAILABLE;

        companion object {
            fun toState(bool: Boolean): NFCState {
                return if (bool) NFC_ENABLED else NFC_DISABLED
            }
        }
    }

    fun checkNFC(): NFCState {
        Log.d(Constant.AR.LOGTAG_PREPARATION, "checking build type(returned '${Build.TYPE}')")

        //Emulators do not support NFC, so we mock this value
        if (BuildConfig.DEBUG) return NFCState.NFC_ENABLED

        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

        return if (nfcAdapter == null) return NFCState.NFC_NOT_AVAILABLE else NFCState.toState(nfcAdapter.isEnabled)
    }
}