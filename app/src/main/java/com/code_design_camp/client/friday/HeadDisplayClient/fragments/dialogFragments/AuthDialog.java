package com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.DefaultSigninFragment;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;


public class AuthDialog extends DialogFragment {
    Dialog dialog;
    ImageButton.OnClickListener dismissdialog = view -> ((MainActivity) getActivity()).dismissSinginPrompt();
    private OnAuthCompletedListener mOnAuthListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    public OnAuthCompletedListener getOnAuthListener() {
        return mOnAuthListener;
    }

    public void setOnAuthListener(OnAuthCompletedListener mOnAuthListener) {
        this.mOnAuthListener = mOnAuthListener;
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
        ft.replace(R.id.signin_fragment_container, new DefaultSigninFragment()).commit();
    }

    public interface OnAuthCompletedListener {
        void onAuthCompleted();
    }
}
