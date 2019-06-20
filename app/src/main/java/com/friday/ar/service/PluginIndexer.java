package com.friday.ar.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Environment;
import android.util.Log;

import com.friday.ar.FridayApplication;
import com.friday.ar.plugin.file.Manifest;
import com.friday.ar.plugin.file.ZippedPluginFile;
import com.friday.ar.plugin.security.PluginVerifier;

import net.lingala.zip4j.exception.ZipException;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarFile;

public class PluginIndexer extends JobService {
    private static final String LOGTAG = "PluginIndexer";
    private ArrayList<JarFile> indexedFiles = new ArrayList<>();


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
                //indexDirectories(defaultDir);
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
                        PluginVerifier.verify(new ZippedPluginFile(file), getApplicationContext());
                        Log.d(LOGTAG, "found valid plugin file:" + file);
                        indexedFiles.add(new JarFile(file));
                    } catch (FileNotFoundException e) {
                        Log.e(LOGTAG, "File was deleted or moved:" + e.getMessage(), e);
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Unknown IOException:" + e.getMessage(), e);
                    } catch (ZipException e) {
                        Log.e(LOGTAG, "Zip file could not be opened:" + e.getLocalizedMessage(), e);
                    } catch (JSONException e) {
                        Log.e(LOGTAG, "Plugin file verification failed:" + e.getLocalizedMessage(), e);
                    } catch (Manifest.ManifestSecurityException e) {
                        Log.e(LOGTAG, "File '" + file.getName() + "' was not added to index; invalid manifest:" + e.getLocalizedMessage(), e);
                    }
                }
            }
        }
    }
}

