package com.code_design_camp.client.friday.HeadDisplayClient.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

import androidx.appcompat.app.AlertDialog;

public class ProgressDialog extends AlertDialog {
    String msg;
    Context context;

    public ProgressDialog(Context context, String message) {
        super(context);
        this.msg = message;
        this.context = context;
        View v = ((Activity) context).getLayoutInflater().inflate(R.layout.loading_dialog, null);
        ((TextView) v.findViewById(R.id.msg_text)).setText(message);
        super.setView(v);
        super.setCancelable(false);
    }
}
