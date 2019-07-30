package com.friday.ar.dialog

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.friday.ar.R

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
    private var messageTextView: TextView

    init {
        messageTextView = v.findViewById(R.id.msg_text)
        messageTextView.text = msg
        super.setView(v)
        super.setCancelable(false)
    }

    fun setMessage(message: String) {
        this.msg = message
        messageTextView.text = message
    }

    fun setMessage(@StringRes stringResource: Int) {
        this.msg = getContext().getString(stringResource)
        messageTextView.text = msg
    }
}
