package com.friday.ar.fragments.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.friday.ar.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ManagerBottomSheetDialogFragment extends BottomSheetDialogFragment {
    public ManagerBottomSheetDialogFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.storemanager_bottom_sheet, container, false);

        return dialogView;
    }
}
