package com.friday.ar.account.data

import android.content.Context
import com.friday.ar.core.Constant

import java.io.File

class UserStore(internal var context: Context) {
    var avatarFile: File = File(context.filesDir.path + Constant.Account.AVATAR_FILE_URI)
        get() {
            if (!field.exists() && field.parentFile != null) field.parentFile!!.mkdirs()
            return field
        }

}
