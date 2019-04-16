package com.friday.ar.plugin;

import android.media.Image;

public class Plugin {
    public String name;
    public Image icon;
    public String authorName;
    public String authorId;
    public String versionName;

    public String getName() {
        return name;
    }

    public Image getIcon() {
        return icon;
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
