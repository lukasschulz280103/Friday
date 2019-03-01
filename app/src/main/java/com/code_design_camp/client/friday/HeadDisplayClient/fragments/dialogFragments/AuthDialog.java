package com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.DefaultSigninFragment;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.interfaces.OnAuthCompletedListener;

public class AuthDialog extends DialogFragment {
    private DefaultSigninFragment signinFragment;

    ImageButton.OnClickListener dismissdialog = view -> dismissDialog();

    public AuthDialog(){
        signinFragment = new DefaultSigninFragment();
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    public OnAuthCompletedListener getOnAuthListener() {
        return signinFragment.getmOnAuthCompletedListener();
    }

    public void setOnAuthListener(OnAuthCompletedListener mOnAuthListener) {
        signinFragment.setOnAuthCompletedListener(mOnAuthListener);
    }

    public void dismissDialog() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(android.R.id.content, new Fragment())
                .commitNow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signin_layout, container, false);
        ImageButton dismissbtn = v.findViewById(R.id.close_signin_dialog);
        dismissbtn.setOnClickListener(dismissdialog);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.signin_fragment_container, signinFragment)
                .commit();
    }

}
