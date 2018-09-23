package com.code_design_camp.client.friday.HeadDisplayClient.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

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
