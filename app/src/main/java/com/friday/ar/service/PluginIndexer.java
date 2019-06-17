package com.friday.ar.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Environment;
import android.util.Log;

import com.friday.ar.FridayApplication;
import com.friday.ar.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginIndexer extends JobService {
    private static final String LOGTAG = "PluginIndexer";
    private ArrayList<JarFile> indexedFiles = new ArrayList<>();

    //TODO: Add deeper verification checks to prevent installations of tamperd plugins
    @SuppressWarnings("StatementWithEmptyBody")
    public static boolean verify(JarFile jar) throws IOException {
        Log.d(LOGTAG, "veryfing file:" + jar.getName());
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            try {
                byte[] buffer = new byte[8192];
                InputStream is = jar.getInputStream(entry);
                while ((is.read(buffer, 0, buffer.length)) != -1) {
                    // We just read. This will throw a SecurityException
                    // if a signature/digest check fails.
                }
            } catch (SecurityException se) {
                Log.e(LOGTAG, "Could not verify jar file:" + se.getMessage(), se);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(LOGTAG, "Stopped indexing job...");
        return true;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(() -> {
            Log.d(LOGTAG, "Started indexing job...");
            try {
                File defaultDir = Environment.getExternalStorageDirectory();
                indexDirectories(defaultDir);
                Log.d(LOGTAG, "indexed Plugins:" + indexedFiles);
                ((FridayApplication) getApplication()).setIndexedInstallablePluginFiles(indexedFiles);
                jobFinished(params, true);
            } catch (Exception e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
            }
        }).start();
        return true;
    }

    private void indexDirectories(File directory) {
        for (File file : directory.listFiles()) {
            if (file.canRead()) {
                if (file.isDirectory()
                        && !file.isFile()) {
                    Log.d(LOGTAG, "Searching in directory:" + directory.getName());
                    indexDirectories(file);
                } else {
                    try {
                        Log.d(LOGTAG, FileUtil.getFileExtension(file));
                        if (FileUtil.getFileExtension(file).equals(".jar") && verify(new JarFile(file))) {
                            Log.d(LOGTAG, "found valid jar file:" + file);
                            indexedFiles.add(new JarFile(file));
                        }
                    } catch (FileNotFoundException e) {
                        Log.e(LOGTAG, e.getMessage(), e);
                    } catch (IOException e) {
                        Log.e(LOGTAG, e.getMessage(), e);
                    }
                }
            }
        }
    }
}

