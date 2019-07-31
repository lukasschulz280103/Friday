package com.friday.ar.util.cache

import android.content.Context
import android.util.Log
import com.friday.ar.Constant
import com.friday.ar.plugin.file.PluginFile
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.security.VerificationSecurityException
import java.io.File
import java.io.IOException
import java.util.zip.ZipException

class PluginFileCacheUtil {
    companion object {
        private const val LOGTAG = "PluginFileCacheUtil"
        @Throws(ZipException::class)
        fun cachePluginFile(context: Context, zippedPluginFile: ZippedPluginFile): PluginFile {
            zippedPluginFile.extractAll(Constant.getPluginCacheDir(context).path)
            Log.d(LOGTAG, Constant.getPluginCacheDir(context, zippedPluginFile.file.name.replace(".fpl", "")).path)
            return PluginFile(Constant.getPluginCacheDir(context, zippedPluginFile.file.name.replace(".fpl", "")).path)
        }

        @Throws(VerificationSecurityException::class)
        fun extractManifest(context: Context, zippedPluginFile: ZippedPluginFile): PluginFile {
            val cacheDirPath = Constant.getPluginCacheDir(context).path
            val cacheDirNewName = zippedPluginFile.file.name.replace(".fpl", "")
            if (!zippedPluginFile.file.exists()) {
                throw IOException("zippedPluginFile does not exist anymore.")
            }
            zippedPluginFile.extractFile(zippedPluginFile.file.path + "/meta/manifest.json", cacheDirPath)
            val manifestFile = File(cacheDirPath + cacheDirNewName + "meta/manifest.json")
            if (!manifestFile.exists()) {
                throw VerificationSecurityException("No manifest file could be extracted; no manifest found in pluginFile $cacheDirNewName")
            }
            Log.d(LOGTAG, Constant.getPluginCacheDir(context, zippedPluginFile.file.name.replace(".fpl", "")).path)
            val pluginFile = PluginFile(Constant.getPluginCacheDir(context, zippedPluginFile.file.name.replace(".fpl", "")).path)
            return pluginFile
        }
    }
}