package com.friday.ar.util.cache

import android.content.Context
import android.util.Log
import com.friday.ar.Constant
import com.friday.ar.plugin.file.PluginFile
import com.friday.ar.plugin.file.ZippedPluginFile
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class CacheUtil {
    companion object {
        private const val LOGTAG = "CacheUtil"
        fun cachePluginFile(context: Context, zippedPluginFile: ZippedPluginFile): PluginFile {
            zippedPluginFile.extractAll(Constant.getPluginCacheDir(context).path)
            val unzippedFile = File(Constant.getPluginCacheDir(context, zippedPluginFile.file.name).path)
            val targetCacheDir = File(Constant.getPluginCacheDir(context, zippedPluginFile.file.name.replace("." + zippedPluginFile.file.extension, "")).path)
            Log.d(LOGTAG, targetCacheDir.list()!!.contentToString())
            Files.move(unzippedFile.toPath(), targetCacheDir.toPath(), StandardCopyOption.REPLACE_EXISTING)
            return PluginFile(targetCacheDir.path)
        }
    }

}