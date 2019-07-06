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

    /**
     * Returns the file ending of the passed [File] object.
     *
     * @param file File to work with
     * @return returns the file extension, e.g. ".jar". Returns empty string if the file has no ending.
     */
    fun getFileExtension(file: File): String {
        if (file.isDirectory)
            throw IllegalArgumentException("Can't get the ending of a directory.")
        return if (file.name.contains(".")) file.name.substring(file.name.lastIndexOf(".")) else ""
    }

    fun deleteDirectory(directory: File) {
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
