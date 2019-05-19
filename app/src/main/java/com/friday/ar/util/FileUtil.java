package com.friday.ar.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {
    private static final String LOGTAG = "FileUtility";

    public static void moveFile(File f, File dest) throws IOException {
        dest.getParentFile().mkdirs();
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(f).getChannel();
            outChannel = new FileOutputStream(dest).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
            f.getParentFile().delete();
        }
    }

    /**
     * Returns the file ending of the passed {@link File} object.
     *
     * @param file File to work with
     * @return returns the file extension, e.g. "jar"
     */
    public static String getFileExtension(File file) {
        if (file.isDirectory())
            throw new IllegalArgumentException("Can't get the ending of a directory.");
        return file.getName().contains(".") ? file.getName().substring(file.getName().lastIndexOf(".")):"";
    }
}
