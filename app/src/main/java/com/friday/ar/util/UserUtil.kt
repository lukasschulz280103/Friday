package com.friday.ar.util

import android.content.Context

import java.io.File

class UserUtil(internal var context: Context) {
    var avatarFile: File = File(context.filesDir.path + "/profile/avatar.jpg")
        get() {
            if (!field.exists() && field.parentFile != null) field.parentFile!!.mkdirs()
            return field
        }

}
