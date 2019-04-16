package com.friday.ar.dialog;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;

import com.friday.ar.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ErrorDialog {
    private Context c;
    private Exception e;
    private MaterialAlertDialogBuilder builder;
    private AlertDialog dialog;

    public ErrorDialog(Context c, @DrawableRes int iconRes, Exception e) {
        this.c = c;
        this.e = e;
        builder = new MaterialAlertDialogBuilder(c);
        builder.setIcon(iconRes);
    }

    public void show() {
        builder.setTitle(R.string.internal_error_title);
        builder.setMessage(e.getMessage());
        builder.setPositiveButton(android.R.string.ok, null);
        dialog = builder.create();
        dialog.show();
    }

    public boolean isShown() {
        return dialog.isShowing();
    }
}
