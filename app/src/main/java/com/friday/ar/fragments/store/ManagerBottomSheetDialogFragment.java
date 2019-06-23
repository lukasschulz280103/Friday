package com.friday.ar.fragments.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.friday.ar.R;
import com.friday.ar.ui.store.packageInstaller.StoreInstallationManagerActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ManagerBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private Activity contextActivity;

    public ManagerBottomSheetDialogFragment(Activity activity) {
        this.contextActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.storemanager_bottom_sheet, container, false);
        dialogView.findViewById(R.id.myInstallations).setOnClickListener(view -> startActivityAnimated(new Intent(getActivity(), StoreInstallationManagerActivity.class), view));
        return dialogView;
    }

    public void startActivityAnimated(Intent intent, View animationTarget) {
        Bundle activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(contextActivity, animationTarget, animationTarget.getTransitionName())
                .toBundle();
        startActivity(intent, activityOptions);
    }
}
