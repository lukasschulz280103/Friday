package com.friday.ar.fragments.dialogFragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.friday.ar.R;
import com.friday.ar.ui.MainActivity;
import com.google.android.material.button.MaterialButton;

import static android.app.Activity.RESULT_OK;

public class UninstallOldAppDialog extends DialogFragment {
    private Dialog dialog;
    private MaterialButton retry;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentLayout = inflater.inflate(R.layout.uninstall_old_dialog, container, false);
        retry = contentLayout.findViewById(R.id.retry_uninstallation);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptUninstall();
            }
        });
        return contentLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promptUninstall();
    }

    public void promptUninstall() {
        Intent unins = new Intent(Intent.ACTION_DELETE);
        unins.setData(Uri.parse("package:com.code_design_camp.client.rasberrypie.rbpieclient"));
        unins.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(unins, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                ((MainActivity) getActivity()).dismissUninstallPrompt();
            }
        }
    }
}
