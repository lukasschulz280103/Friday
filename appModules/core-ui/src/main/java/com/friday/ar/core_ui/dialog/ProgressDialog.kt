package com.friday.ar.core_ui.dialog

import android.app.Activity
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.friday.ar.core_ui.R
import kotlinx.android.synthetic.main.loading_dialog.*

/**
 * A custom ProgressDialog.
 * Androids internal [android.app.ProgressDialog] is deprecated due to the change of the material design guidelines.
 *
 * @see android.app.ProgressDialog
 *
 * @see [Material guidelines](material.io)
 */
class ProgressDialog(internal var context: Activity, private var msg: String) : AlertDialog(context) {
    internal var v: View = (context).layoutInflater.inflate(R.layout.loading_dialog, null)

    init {
        msg_text.text = msg
        super.setView(v)
        super.setCancelable(false)
    }

    fun setMessage(message: String) {
        this.msg = message
        msg_text.text = message
    }

    fun setMessage(@StringRes stringResource: Int) {
        this.msg = getContext().getString(stringResource)
        msg_text.text = msg
    }
}
