package com.code_design_camp.client.friday.HeadDisplayClient.fragments.templateClasses;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.code_design_camp.client.friday.HeadDisplayClient.fragments.interfaces.OnAuthCompletedListener;


public class AuthFragment extends Fragment implements OnAuthCompletedListener {
    @Override
    public void onAuthCompleted() {

    }

    @Override
    public void onCanceled() {

    }

    public class AuthDialogFragment extends DialogFragment implements OnAuthCompletedListener {

        @Override
        public void onAuthCompleted() {

        }

        @Override
        public void onCanceled() {

        }
    }
}