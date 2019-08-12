package com.friday.ar.util.cache

import android.content.Context
import android.util.Log
import com.friday.ar.Constant
import com.friday.ar.plugin.file.Manifest
import com.friday.ar.plugin.file.PluginFile
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.security.VerificationSecurityException
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.FileHeader
import org.json.JSONObject
import java.nio.charset.Charset

class PluginFileCacheUtil {
    companion object {
        private const val LOGTAG = "PluginFileCacheUtil"
        @Throws(ZipException::class)
        fun cachePluginFile(context: Context, zippedPluginFile: ZippedPluginFile): PluginFile {
            zippedPluginFile.extractAll(Constant.getPluginCacheDir(context).path)
            Log.d(LOGTAG, Constant.getPluginCacheDir(context, zippedPluginFile.file.name.replace(".fpl", "")).path)
            return if (zippedPluginFile.fileHeaders.size != 0) {
                val extractedFileName = (zippedPluginFile.fileHeaders[0] as FileHeader).fileName
                PluginFile(Constant.getPluginCacheDir(context, extractedFileName).path)
            } else PluginFile(zippedPluginFile.file.name.replace(".fpl", ""))
        }

        @Throws(VerificationSecurityException::class, ZipException::class, IllegalStateException::class)
        fun extractManifest(context: Context, zippedPluginFile: ZippedPluginFile): Manifest {
            if (!zippedPluginFile.isValidZipFile) throw IllegalStateException("This plugin installation file is invalid.")
            if (zippedPluginFile.isEncrypted) throw IllegalArgumentException("Cannot read from an encrypted plugin installation file.")

            val shortenedName = zippedPluginFile.file.name.replace(".fpl", "")
            val manifestFileHeader = zippedPluginFile.getFileHeader("$shortenedName/meta/manifest.json")
                    ?: throw VerificationSecurityException("No manifest was found.")

            val manifestInputStream = zippedPluginFile.getInputStream(manifestFileHeader)
            val manifestString = manifestInputStream.readBytes().toString(Charset.defaultCharset())
            if (manifestString.isEmpty()) throw VerificationSecurityException("manifest was found, but is empty")

            return Manifest.fromJSON(JSONObject(manifestString))
        }
    }
}