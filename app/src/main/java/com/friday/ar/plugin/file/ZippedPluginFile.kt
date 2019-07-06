package com.friday.ar.plugin.file


import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException


import java.io.File

class ZippedPluginFile @Throws(ZipException::class)
constructor(zipFile: File) : ZipFile(zipFile)
