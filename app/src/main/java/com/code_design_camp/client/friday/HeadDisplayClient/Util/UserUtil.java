package com.code_design_camp.client.friday.HeadDisplayClient.Util;

import android.content.Context;

import java.io.File;

public class UserUtil {
    File avatarFile;
    Context context;

    public UserUtil(Context context) {
        this.context = context;
        avatarFile = new File(context.getFilesDir(), File.separator + "profile" + File.separator + "avatar.jpg");
    }

    public File getAvatarFile() {
        return avatarFile;
    }

    public void setNewUserAvatarFile(File avatarFile) {

    }
}
