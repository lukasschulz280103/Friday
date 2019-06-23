package com.friday.ar.dialog.store.packageInstaller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.friday.ar.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * This dialog shows up when an extrernal file browser app sends an intent to this app.
 */
public class PackageInstallerDialog extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.package_installer_dialog, container);
        return dialogView;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().finish();
    }
}
