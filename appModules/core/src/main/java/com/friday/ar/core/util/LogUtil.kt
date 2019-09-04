package com.friday.ar.core.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.friday.ar.core.BuildConfig
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.util.*

object LogUtil {

    private const val VERSION_NAME = BuildConfig.VERSION_NAME
    private const val VERSION_CODE = BuildConfig.VERSION_CODE
    private val BUILD_DEBUG = BuildConfig.DEBUG
    private const val BUILD_TYPE = BuildConfig.BUILD_TYPE
    private val DEVICE = Build.DEVICE!!
    private val MODEL = Build.MODEL!!
    private val BRAND = Build.BRAND!!
    private val SDK_VERSION = Build.VERSION.SDK_INT
    private val BASE_OS: String? = Build.VERSION.BASE_OS
    private val TAGS = Build.TAGS!!
    private val MANUFACTURER = Build.MANUFACTURER!!
    private val PRODUCT = Build.PRODUCT!!
    private val BUID_TIME = Build.TIME
    private const val LOGTAG = "LogUtil"

    fun createDebugInfoFile(c: Context, vararg keyValueArgs: Any): File? {
        val temporaryLogFiles = c.cacheDir
        try {
            val deviceInfoFile = File.createTempFile("log", "json", temporaryLogFiles)
            val out = FileWriter(deviceInfoFile)
            val deviceInfo = JSONObject()
            var i = 0
            while (i < keyValueArgs.size) {
                Log.d(LOGTAG, deviceInfo.toString())
                deviceInfo.put(keyValueArgs[i] as String, keyValueArgs[i + 1])
                i += 2
            }
            deviceInfo.put("app_build_isDebug", BUILD_DEBUG)
            deviceInfo.put("app_build_type", BUILD_TYPE)
            deviceInfo.put("app_version_name", VERSION_NAME)
            deviceInfo.put("app_version_code", VERSION_CODE)
            deviceInfo.put("freeSpace", temporaryLogFiles.freeSpace)
            deviceInfo.put("device", DEVICE)
            deviceInfo.put("model", MODEL)
            deviceInfo.put("brand", BRAND)
            deviceInfo.put("product", PRODUCT)
            deviceInfo.put("sdkVersion", SDK_VERSION)
            deviceInfo.put("base_os", BASE_OS)
            deviceInfo.put("manufacturer", MANUFACTURER)
            deviceInfo.put("tags", TAGS)
            deviceInfo.put("time", BUID_TIME)
            Log.d("DeviceInfo", deviceInfo.toString())
            out.write(deviceInfo.toString())
            out.close()
            return deviceInfoFile
        } catch (e: IOException) {
            Log.e(LOGTAG, e.localizedMessage, e)
            return null
        } catch (e: JSONException) {
            Log.e(LOGTAG, e.localizedMessage, e)
            return null
        }

    }

    @Deprecated("Use readText() instead")
    @Throws(FileNotFoundException::class)
    fun fileToString(logFile: File): String {
        val sc = Scanner(logFile)
        var content = ""
        while (sc.hasNext()) {
            content += sc.next()
        }
        Log.d(LOGTAG, "Executed logutil filestostring")
        Log.d(LOGTAG, "DeviceInfoFileContent: $content")
        return content
    }
}
