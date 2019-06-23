package com.friday.ar.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {
    private static final String LOGTAG = "FileUtility";

    public static void moveFile(File f, File dest) throws IOException {
        dest.getParentFile().mkdirs();
        if (!f.exists()) {
            throw new FileNotFoundException("The source file does not exist!\n\nsource file path:" + f.getPath());
        }
        try (FileChannel inChannel = new FileInputStream(f).getChannel(); FileChannel outChannel = new FileOutputStream(dest).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        } finally {
            f.getParentFile().delete();
        }
    }

    /**
     * Returns the file ending of the passed {@link File} object.
     *
     * @param file File to work with
     * @return returns the file extension, e.g. ".jar". Returns empty string if the file has no ending.
     */
    public static String getFileExtension(File file) {
        if (file.isDirectory())
            throw new IllegalArgumentException("Can't get the ending of a directory.");
        return file.getName().contains(".") ? file.getName().substring(file.getName().lastIndexOf(".")):"";
    }
}
