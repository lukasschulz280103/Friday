package com.friday.ar.plugin.file;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

public class ZippedPluginFile extends ZipFile {
    public ZippedPluginFile(String zipFile) throws ZipException {
        super(zipFile);
    }

    public ZippedPluginFile(File zipFile) throws ZipException {
        super(zipFile);
    }
}
