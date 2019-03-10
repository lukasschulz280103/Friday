package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Util.FileUtil;
import com.code_design_camp.client.friday.HeadDisplayClient.Util.UserUtil;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.interfaces.OnAuthCompletedListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class DefaultSigninFragment extends Fragment {
    public static final String LOGTAG = "DeafultSigninFragment";
    private static final int RC_SIGN_IN = 9001;
    private TextView resetResultText, resetPasswText;
    private OnAuthCompletedListener mOnAuthCompletedListener;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private MaterialAlertDialogBuilder loading_dialog_builder;
    private AlertDialog loading_dialog;
    private TextInputEditText emailinput;
    private TextInputLayout email_input_wrapper;
    private SignInButton google_signin;
    private GoogleSignInClient mSignInClient;
    private ProgressBar loader;
    private TextInputEditText passwEdittext;
    private MaterialButton submitbtn;
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
            Snackbar.make(getActivity().getCurrentFocus(), getString(R.string.signin_welcome, emailinput.getText().toString()), Snackbar.LENGTH_SHORT).show();
            mOnAuthCompletedListener.onAuthCompleted();
        } else {
            MaterialAlertDialogBuilder errdialogbuilder = new MaterialAlertDialogBuilder(getContext());
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
                    errdialogbuilder.setMessage(getString(R.string.exception_user_not_found_msg, emailinput.getText().toString()));
                    errdialogbuilder.setNeutralButton(R.string.action_createnew_account, (dialogInterface, i) -> {
                        Task crUser = auth.createUserWithEmailAndPassword(emailinput.getText().toString(), passwEdittext.getText().toString());
                        crUser.addOnCompleteListener(crnewUserCompletion);
                        loader.setVisibility(View.VISIBLE);
                        setInputsEnabled(false);
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
    SignInButton.OnClickListener requestSignin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent signInIntent = mSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };
    private Activity mActivity;
    TextInputEditText.OnEditorActionListener mOnEditorActionListener = (textView, i, keyEvent) -> {
        if (i == EditorInfo.IME_ACTION_DONE) {
            validateEmail();
            return true;
        }
        return false;
    };

    public DefaultSigninFragment() {
        //required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentview = inflater.inflate(R.layout.default_signin_fragment_layout, container, false);
        mActivity = getActivity();
        passwEdittext = fragmentview.findViewById(R.id.email_password);
        resetPasswText = fragmentview.findViewById(R.id.password_forgot);
        submitbtn = fragmentview.findViewById(R.id.submit);
        loader = fragmentview.findViewById(R.id.signin_progress);
        email_input_wrapper = fragmentview.findViewById(R.id.email_input_layout);
        emailinput = fragmentview.findViewById(R.id.email_input_signin);
        google_signin = fragmentview.findViewById(R.id.signin_google_button);
        submitbtn.setOnClickListener(view -> {
            validateEmail();
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.request_id_token))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(mActivity, gso);
        google_signin.setOnClickListener(requestSignin);
        resetPasswText.setOnClickListener(view -> resetPassword());
        emailinput.setOnEditorActionListener(mOnEditorActionListener);
        return fragmentview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loading_dialog_builder = new MaterialAlertDialogBuilder(getContext());
        loading_dialog_builder.setView(R.layout.loading_dialog);
        loading_dialog_builder.setCancelable(false);
        loading_dialog = loading_dialog_builder.create();
        loading_dialog.show();
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e);
                loading_dialog.dismiss();
                MaterialAlertDialogBuilder apierrrdialog = new MaterialAlertDialogBuilder(getContext());
                apierrrdialog.setTitle(e.getStatusCode() + ": " + getString(R.string.internal_error_title));
                apierrrdialog.setMessage(e.getMessage());
                apierrrdialog.setPositiveButton(android.R.string.ok, null);
                apierrrdialog.create().show();

            }
        }

    }

    public void setOnAuthCompletedListener(OnAuthCompletedListener mOnAuthCompletedListener) {
        this.mOnAuthCompletedListener = mOnAuthCompletedListener;
    }

    public OnAuthCompletedListener getmOnAuthCompletedListener() {
        return mOnAuthCompletedListener;
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("CONTEXT", "Activity is " + mActivity);
                        DownloadManager downloadManager = (DownloadManager) mActivity.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        DownloadManager.Request dmrequest = new DownloadManager.Request(user.getPhotoUrl());
                        dmrequest.setVisibleInDownloadsUi(false);
                        dmrequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                        dmrequest.setDestinationInExternalFilesDir(mActivity, "profile", File.separator + "avatar.jpg");
                        mActivity.registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                loading_dialog.dismiss();
                                UserUtil userUtil = new UserUtil(context);
                                File avatar_temp = new File(mActivity.getExternalFilesDir("profile"), "avatar.jpg");
                                File avatar_dest = userUtil.getAvatarFile();
                                try {
                                    FileUtil.moveFile(avatar_temp, avatar_dest);
                                } catch (IOException e) {
                                    Log.e("AuthDialog", e.getLocalizedMessage(), e);
                                }
                                mOnAuthCompletedListener.onAuthCompleted();
                            }
                        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        downloadManager.enqueue(dmrequest);
                        Snackbar.make(mActivity.findViewById(android.R.id.content), getString(R.string.signin_welcome, user.getDisplayName()), Snackbar.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(mActivity.findViewById(android.R.id.content), "Sign in error.", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void setInputsEnabled(boolean enabled) {
        emailinput.setEnabled(enabled);
        passwEdittext.setEnabled(enabled);
        resetPasswText.setEnabled(enabled);
        submitbtn.setEnabled(enabled);
    }

    private boolean validateEmail() {
        String text = emailinput.getText().toString();
        if (!text.contains(".") || !text.contains("@")) {
            email_input_wrapper.setError(getString(R.string.mail_invalid_error));
            return false;
        } else {
            loader.setVisibility(View.VISIBLE);
            setInputsEnabled(false);
            auth.signInWithEmailAndPassword(emailinput.getText().toString(), passwEdittext.getText().toString()).addOnCompleteListener(completionlistener);
            return true;
        }
    }

    private void resetPassword() {
        MaterialAlertDialogBuilder askResetPassword = new MaterialAlertDialogBuilder(getContext());
        askResetPassword.setTitle(R.string.auth_forgot_password);
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.reset_password_dialog, null, false);
        ((TextView) dialogView.findViewById(R.id.email)).setText(emailinput.getText().toString());
        resetResultText = dialogView.findViewById(R.id.reset_result_text);
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
                Task resetPswd = auth.sendPasswordResetEmail(emailinput.getText().toString());
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
