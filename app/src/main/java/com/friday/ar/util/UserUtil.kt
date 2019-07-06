package com.friday.ar.util

import android.content.Context

import java.io.File

class UserUtil(internal var context: Context) {
    var avatarFile: File
        internal set

    init {
        avatarFile = File(context.filesDir, File.separator + "profile" + File.separator + "avatar.jpg")
    }

    fun setNewUserAvatarFile(avatarFile: File) {

    }
}
