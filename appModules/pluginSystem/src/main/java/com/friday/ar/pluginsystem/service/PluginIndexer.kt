package com.friday.ar.pluginsystem.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.friday.ar.core.Constant
import com.friday.ar.pluginsystem.cache.PluginFileCacheUtil
import com.friday.ar.pluginsystem.file.ZippedPluginFile
import com.friday.ar.pluginsystem.security.PluginVerifier
import com.friday.ar.pluginsystem.security.VerificationSecurityException
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.util.*

/**
 * This class automatically indexes plugins after searching for them on the internal storage.
 * <b>It does not list installed plugins.</b>
 */
class PluginIndexer : JobService() {
    companion object {
        private const val LOGTAG = "InstallablePluginIndexer"
    }

    private val indexedFiles = ArrayList<ZippedPluginFile>()
    private val excludedDirs = listOf("Android")
    private var isIndexing = false

    override fun onStopJob(params: JobParameters): Boolean {
        Log.d(LOGTAG, "Stopped indexing job...")
        return true
    }

    override fun onStartJob(params: JobParameters): Boolean {
        Thread {
            Log.d(LOGTAG, "Started indexing job...")
            try {
                val defaultDir = Environment.getExternalStorageDirectory()
                isIndexing = true
                indexDirectories(defaultDir)
                isIndexing = false
                Log.d(LOGTAG, "indexed Plugins:$indexedFiles")
                val pluginIndexerBroadcastIntent = Intent(Constant.BroadcastReceiverActions.BROADCAST_PLUGINS_INDEXED)
                pluginIndexerBroadcastIntent.putExtra("indexList", indexedFiles)
                sendBroadcast(pluginIndexerBroadcastIntent)
                jobFinished(params, true)
            } catch (e: Exception) {
                Log.e(LOGTAG, e.localizedMessage, e)
            }
        }.start()
        return true
    }

    private fun indexDirectories(directory: File) {
        if (directory.listFiles() == null) {
            Log.i(LOGTAG, "no installed plugins found")
            return
        }

        for (file in directory.listFiles { pathname ->
            pathname.isDirectory && !excludedDirs.contains(pathname.name)
        }!!) {
            if (file.canRead()) {
                if (file.isDirectory && !file.isFile) {
                    indexDirectories(file)
                } else {
                    try {
                        val verifier = PluginVerifier()
                        verifier.setOnVerificationCompleteListener(object : PluginVerifier.OnVerificationCompleteListener {
                            override fun onSuccess() {
                                try {
                                    indexedFiles.add(ZippedPluginFile(file))
                                } catch (e: ZipException) {
                                    Log.e(LOGTAG, e.localizedMessage, e)
                                }

                            }

                            override fun onZipException(e: ZipException) {
                                Log.e(LOGTAG, "Zip file '" + file.name + "'could not be opened:" + e.localizedMessage)
                            }

                            override fun onIOException(e: IOException) {
                                Log.e(LOGTAG, "Unknown IOException:" + e.message, e)
                            }

                            override fun onJSONException(e: JSONException) {
                                Log.e(LOGTAG, "Plugin file verification failed(invalid json):" + e.localizedMessage)
                            }

                            override fun onVerificationFailed(e: VerificationSecurityException) {
                                Log.e(LOGTAG, "File '" + file.name + "' was not added to index; invalid manifest:" + e.localizedMessage)
                            }
                        })
                        verifier.verify(PluginFileCacheUtil.cachePluginFile(applicationContext, ZippedPluginFile(file)), true)
                    } catch (e: ZipException) {
                        Log.e(LOGTAG, e.localizedMessage, e)
                    }

                }
            }
        }
    }
}
