package com.friday.ar.dialog

import android.content.Context

import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog

import com.friday.ar.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ErrorDialog(c: Context, @DrawableRes iconRes: Int, private val e: Exception) {
    private val builder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(c)
    private var dialog: AlertDialog? = null

    val isShown: Boolean
        get() = dialog!!.isShowing

    init {
        builder.setIcon(iconRes)
    }

    fun show() {
        builder.setTitle(R.string.internal_error_title)
        builder.setMessage(e.message)
        builder.setPositiveButton(android.R.string.ok, null)
        dialog = builder.create()
        dialog!!.show()
    }
}
