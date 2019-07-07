package com.friday.ar.util

import android.util.Log
import java.io.*

object FileUtil {
    private val LOGTAG = "FileUtility"

    @Throws(IOException::class)
    fun moveFile(f: File, dest: File) {
        dest.parentFile.mkdirs()
        if (!f.exists()) {
            throw FileNotFoundException("The source file does not exist!\n\nsource file path:" + f.path)
        }
        try {
            FileInputStream(f).channel.use { inChannel -> FileOutputStream(dest).channel.use { outChannel -> inChannel.transferTo(0, inChannel.size(), outChannel) } }
        } catch (e: IOException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        } finally {
            f.parentFile.delete()
        }
    }


    fun deleteDirectory(directory: File?) {
        if (directory?.listFiles() == null) return
        for (fileOrDirectory in directory.listFiles()) {
            if (fileOrDirectory.isDirectory) {
                deleteDirectory(fileOrDirectory)
            } else {
                fileOrDirectory.delete()
            }
        }
        directory.delete()
    }
}
