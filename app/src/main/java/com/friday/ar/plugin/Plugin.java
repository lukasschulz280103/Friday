package com.friday.ar.plugin;

import android.media.Rating;
import android.net.Uri;

import java.io.File;

public class Plugin {
    private String name;
    private Uri iconURI;
    private String authorName;
    private String authorId;
    private String versionName;
    private Rating rating;
    private PluginFile pluginFile;

    public PluginFile getPluginFile() {
        return pluginFile;
    }

    public Rating getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public Uri getIconURI() {
        return iconURI;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getVersionName() {
        return versionName;
    }

    public class PluginFile extends File {
        public PluginFile(String packageName) {
            super(packageName);
        }

        @Override
        public long length() {
            return super.length();
        }
    }
}
