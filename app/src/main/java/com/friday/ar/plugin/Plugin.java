package com.friday.ar.plugin;

import android.media.Rating;
import android.net.Uri;

import com.friday.ar.plugin.file.PluginFile;

public class Plugin {
    public void setName(String name) {
        this.name = name;
    }

    public void setIconURI(Uri iconURI) {
        this.iconURI = iconURI;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setPluginFile(PluginFile pluginFile) {
        this.pluginFile = pluginFile;
    }

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
}
