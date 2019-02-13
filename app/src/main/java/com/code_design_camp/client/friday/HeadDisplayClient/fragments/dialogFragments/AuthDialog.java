package com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.DefaultSigninFragment;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.list.DashboardSimpleItem;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.MainActivity;


public class AuthDialog extends DialogFragment {
    private DefaultSigninFragment signinFragment;
    private OnAuthCompletedListener mOnAuthListener;

    ImageButton.OnClickListener dismissdialog = view -> mOnAuthListener.onCanceled();

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
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Log.d("AuthDialog", "oncreatedialog getDialog = " + getDialog());
        Log.d("AuthDialog", "onCreateDialog");
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
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.signin_fragment_container, signinFragment).commit();
    }

    public interface OnAuthCompletedListener {
        void onAuthCompleted();
        void onCanceled();
    }
}
