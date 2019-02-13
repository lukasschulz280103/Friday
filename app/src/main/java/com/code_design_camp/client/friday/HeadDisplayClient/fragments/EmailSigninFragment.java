package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EmailSigninFragment extends Fragment {
    private static final String LOGTAG = "EmailSigninFragment";
    private ProgressBar loader;
    private TextInputEditText passwEdittext;
    private MaterialButton backbtn, submitbtn;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String email;
    private DefaultSigninFragment signinFragment;
    private OnCompleteListener crnewUserCompletion = task -> {
        loader.setVisibility(View.INVISIBLE);
        setInputsEnabled(true);
        if (task.isSuccessful()) {

        } else {
            try {
                throw task.getException();
            } catch (Exception e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
            }
        }
    };
    private OnCompleteListener completionlistener = task -> {
        loader.setVisibility(View.INVISIBLE);
        setInputsEnabled(true);
        if (task.isSuccessful()) {
            Snackbar.make(getActivity().getCurrentFocus(), getString(R.string.signin_welcome, email), Snackbar.LENGTH_SHORT).show();
            signinFragment.getmOnAuthCompletedListener().onAuthCompleted();
        } else {
            AlertDialog.Builder errdialogbuilder = new AlertDialog.Builder(getContext());
            try {
                throw task.getException();
            } catch (FirebaseAuthEmailException e) {
                errdialogbuilder.setTitle(R.string.exception_email_title);
                errdialogbuilder.setMessage(getString(R.string.exception_email_msg, e.getLocalizedMessage()));
            } catch (FirebaseAuthInvalidCredentialsException e) {
                errdialogbuilder.setTitle(R.string.exception_invalid_credentials_title);
                errdialogbuilder.setMessage(getString(R.string.exception_invalid_credentials_msg, e.getLocalizedMessage()));
            } catch (FirebaseAuthInvalidUserException e) {
                String code = e.getErrorCode();
                if (code.equals("ERROR_USER_DISABLED")) {
                    errdialogbuilder.setTitle(R.string.exception_user_disabled_title);
                    errdialogbuilder.setMessage(R.string.exception_user_disabled_msg);
                    errdialogbuilder.setNeutralButton(R.string.more, null);
                } else if (code.equals("ERROR_USER_NOT_FOUND")) {
                    errdialogbuilder.setTitle(R.string.exception_user_not_found_title);
                    errdialogbuilder.setMessage(getString(R.string.exception_user_not_found_msg, email));
                    errdialogbuilder.setNeutralButton(R.string.action_createnew_account, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Task crUser = auth.createUserWithEmailAndPassword(email, passwEdittext.getText().toString());
                            crUser.addOnCompleteListener(crnewUserCompletion);
                            loader.setVisibility(View.VISIBLE);
                            setInputsEnabled(false);
                        }
                    });
                } else {
                    errdialogbuilder.setTitle(e.getErrorCode());
                    errdialogbuilder.setMessage(e.getLocalizedMessage());
                }
            } catch (Exception e) {
                errdialogbuilder.setTitle(e.getMessage());
                errdialogbuilder.setTitle(e.getLocalizedMessage());
                Log.e(LOGTAG, "Unknown error occured while user tried to sign in:" + e.getMessage(), e);

            }
            errdialogbuilder.setPositiveButton(android.R.string.ok, null);
            errdialogbuilder.create().show();
        }
    };

    static EmailSigninFragment newInstance(String email) {
        Bundle args = new Bundle();
        args.putString("email", email);
        EmailSigninFragment fragment = new EmailSigninFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentview = inflater.inflate(R.layout.emailsignin_layout, container, false);
       signinFragment = ((DefaultSigninFragment) getParentFragment());
        email = getArguments().getString("email");
        //"Final one element array"
        passwEdittext = fragmentview.findViewById(R.id.email_password);
        ((TextView) fragmentview.findViewById(R.id.email_head)).setText(email);
        fragmentview.findViewById(R.id.password_forgot).setOnClickListener(view -> resetPassword());
        backbtn = fragmentview.findViewById(R.id.back);
        submitbtn = fragmentview.findViewById(R.id.submit);
        loader = fragmentview.findViewById(R.id.signin_progress);
        backbtn.setOnClickListener(view -> getFragmentManager().popBackStack());
        submitbtn.setOnClickListener(view -> {
            loader.setVisibility(View.VISIBLE);
            setInputsEnabled(false);
            Task signinTask = auth.signInWithEmailAndPassword(email, passwEdittext.getText().toString());
            signinTask.addOnCompleteListener(completionlistener);
        });
        return fragmentview;
    }

    private void setInputsEnabled(boolean enabled) {
        passwEdittext.setEnabled(enabled);
        backbtn.setEnabled(enabled);
        submitbtn.setEnabled(enabled);
    }

    private void resetPassword() {
        AlertDialog.Builder askResetPassword = new AlertDialog.Builder(getContext());
        askResetPassword.setTitle(R.string.auth_forgot_password);
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.reset_password_dialog, null, false);
        ((TextView) dialogView.findViewById(R.id.email)).setText(email);
        TextView resetResultText = dialogView.findViewById(R.id.reset_result_text);
        ViewFlipper flipper = dialogView.findViewById(R.id.viewflipper);
        askResetPassword.setView(dialogView);
        askResetPassword.setPositiveButton(R.string.auth_send_reset_pswd_email, null);
        askResetPassword.setNegativeButton(android.R.string.cancel, null);
        AlertDialog askResetPasswordDialog = askResetPassword.create();
        askResetPasswordDialog.setOnShowListener(dialogInterface -> {
            Button positive = askResetPasswordDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negative = askResetPasswordDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            positive.setOnClickListener((view) -> {
                askResetPasswordDialog.setCancelable(false);
                Task resetPswd = auth.sendPasswordResetEmail(email);
                flipper.showNext();
                positive.setEnabled(false);
                negative.setVisibility(View.GONE);
                resetPswd.addOnCompleteListener(task -> {
                    positive.setEnabled(true);
                    flipper.showNext();
                    positive.setText(android.R.string.ok);
                    if (task.isSuccessful()) {
                        resetResultText.setText(R.string.email_reset_succes);
                    } else {
                        resetResultText.setText(getString(R.string.email_reset_error, task.getException().getLocalizedMessage()));
                    }
                    positive.setOnClickListener(view1 -> askResetPasswordDialog.dismiss());
                });
            });

        });
        askResetPasswordDialog.show();
    }
}
