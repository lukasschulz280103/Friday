package com.code_design_camp.client.friday.HeadDisplayClient.Util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {
    private static final String LOGTAG = "FileUtility";

    public static void moveFile(File f, File dest) throws IOException {
        if (!dest.exists()) {
            if (!dest.mkdir()) {
                return;
            }
        }
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
}
