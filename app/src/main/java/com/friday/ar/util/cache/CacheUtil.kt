package com.friday.ar.util.cache

import android.content.Context
import android.util.Log
import com.friday.ar.Constant
import com.friday.ar.plugin.file.PluginFile
import com.friday.ar.plugin.file.ZippedPluginFile

class CacheUtil {
    companion object {
        private const val LOGTAG = "CacheUtil"
        fun cachePluginFile(context: Context, zippedPluginFile: ZippedPluginFile): PluginFile {
            zippedPluginFile.extractAll(Constant.getPluginCacheDir(context).path)
            Log.d(LOGTAG, Constant.getPluginCacheDir(context, zippedPluginFile.file.name.replace(".fpl", "")).path)
            return PluginFile(Constant.getPluginCacheDir(context, zippedPluginFile.file.name.replace(".fpl", "")).path)
        }
    }

}