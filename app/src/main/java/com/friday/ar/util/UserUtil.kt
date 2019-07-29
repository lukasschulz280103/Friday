package com.friday.ar.util

import android.content.Context

import java.io.File

class UserUtil(internal var context: Context) {
    var avatarFile: File = File(context.filesDir, "/profile/avatar.jpg")

    fun setNewUserAvatarFile(avatarFile: File) {

    }
}
