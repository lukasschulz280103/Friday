package com.friday.ar.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.friday.ar.R;

/**
 * A custom ProgressDialog.
 * Androids internal {@link android.app.ProgressDialog} is deprecated due to the change of the material design guidelines.
 *
 * @see android.app.ProgressDialog
 * @see <a href="material.io">Material guidelines</a>
 */
public class ProgressDialog extends AlertDialog {
    String msg;
    Context context;
    View v;
    TextView message_text;

    public ProgressDialog(Context context, String message) {
        super(context);
        this.msg = message;
        this.context = context;
        v = ((Activity) context).getLayoutInflater().inflate(R.layout.loading_dialog, null);
        message_text = v.findViewById(R.id.msg_text);
        message_text.setText(message);
        super.setView(v);
        super.setCancelable(false);
    }

    public void setMessage(String message) {
        this.msg = message;
        message_text.setText(message);
    }

    public void setMessage(@StringRes int stringResource) {
        this.msg = getContext().getString(stringResource);
        message_text.setText(msg);
    }
}
