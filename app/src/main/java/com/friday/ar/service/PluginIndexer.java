package com.friday.ar.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Environment;
import android.util.Log;

import com.friday.ar.util.FileUtil;

import java.io.File;
import java.util.ArrayList;

public class PluginIndexer extends JobService {
    private static final String LOGTAG = "PluginIndexer";
    private ArrayList<File> indexedFiles = new ArrayList<>();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(LOGTAG, "Started indexing job...");
        try {
            File defaultDir = Environment.getExternalStorageDirectory();
            indexDirectories(defaultDir);
            jobFinished(params, true);
        } catch (Exception e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(LOGTAG, "Stopped indexing job...");
        return true;
    }

    private void indexDirectories(File directory) {
        for (File file : directory.listFiles()) {
            if (file.canRead()) {
                if (file.isDirectory()
                        && !file.isFile()) {
                    Log.d(LOGTAG, "Searching in directory:" + directory.getName());
                    indexDirectories(file);
                } else if (FileUtil.getFileExtension(file).equals("jar")) {
                    Log.d(LOGTAG, "Found matching file:" + file.getName());
                    indexedFiles.add(file);
                }
            }
        }
    }
}

