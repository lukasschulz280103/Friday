package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Util.FileUtil;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.AuthDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class DefaultSigninFragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;
    private AuthDialog.OnAuthCompletedListener mOnAuthCompletedListener;
    private FirebaseAuth mAuth;
    private AlertDialog.Builder loading_dialog_builder;
    private AlertDialog loading_dialog;
    private TextInputEditText emailinput;
    private TextInputLayout email_input_wrapper;
    private AppCompatImageButton submit_form;
    private SignInButton google_signin;
    private GoogleSignInClient mSignInClient;
    SignInButton.OnClickListener requestSignin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent signInIntent = mSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };
    private Activity mActivity;
    AppCompatImageButton.OnClickListener submitBtnClick = view -> validateEmail();
    TextInputEditText.OnEditorActionListener mOnEditorActionListener = (textView, i, keyEvent) -> {
        if (i == EditorInfo.IME_ACTION_DONE) {
            validateEmail();
            return true;
        }
        return false;
    };
    private View fragmentview;

    public DefaultSigninFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.request_id_token))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(mActivity, gso);
        fragmentview = inflater.inflate(R.layout.default_signin_fragment_layout, container, false);

        email_input_wrapper = fragmentview.findViewById(R.id.email_input_layout);
        emailinput = fragmentview.findViewById(R.id.email_input_signin);
        submit_form = fragmentview.findViewById(R.id.submit_form);
        google_signin = fragmentview.findViewById(R.id.signin_google_button);

        google_signin.setOnClickListener(requestSignin);
        submit_form.setOnClickListener(submitBtnClick);
        emailinput.setOnEditorActionListener(mOnEditorActionListener);
        mAuth = FirebaseAuth.getInstance();
        return fragmentview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loading_dialog_builder = new AlertDialog.Builder(getContext());
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
                AlertDialog.Builder apierrrdialog = new AlertDialog.Builder(getContext());
                apierrrdialog.setTitle(e.getStatusCode() + ": " + getString(R.string.internal_error_title));
                apierrrdialog.setMessage(e.getMessage());
                apierrrdialog.setPositiveButton(android.R.string.ok, null);
                apierrrdialog.create().show();

            }
        }

    }

    public void setOnAuthCompletedListener(AuthDialog.OnAuthCompletedListener mOnAuthCompletedListener) {
        this.mOnAuthCompletedListener = mOnAuthCompletedListener;
    }

    public AuthDialog.OnAuthCompletedListener getmOnAuthCompletedListener() {
        return mOnAuthCompletedListener;
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("CONTEXT", "Activity is " + mActivity);
                        DownloadManager downloadManager = (DownloadManager) mActivity.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        DownloadManager.Request dmrequest = new DownloadManager.Request(user.getPhotoUrl());
                        dmrequest.setVisibleInDownloadsUi(false);
                        dmrequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                        dmrequest.setDestinationInExternalFilesDir(getContext(), "profile", File.separator + "avatar.jpg");
                        mActivity.registerReceiver(new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                loading_dialog.dismiss();
                                File avatar_temp = new File(getContext().getExternalFilesDir("profile"), "avatar.jpg");
                                File avatar_dest = new File(getContext().getFilesDir() + File.separator + "profile", "avatar.jpg");
                                try {
                                    FileUtil.moveFile(avatar_temp, avatar_dest);
                                } catch (IOException e) {
                                    Log.e("AuthDialog", e.getLocalizedMessage(), e);
                                }
                                mOnAuthCompletedListener.onAuthCompleted();
                            }
                        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        downloadManager.enqueue(dmrequest);
                        Snackbar.make(mActivity.findViewById(R.id.main_bottom_nav), getString(R.string.signin_welcome, user.getDisplayName()), Snackbar.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(mActivity.findViewById(R.id.main_bottom_nav), "Sign in error.", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateEmail() {
        String text = emailinput.getText().toString();
        if (!text.contains(".") || !text.contains("@")) {
            email_input_wrapper.setError(getString(R.string.mail_invalid_error));
        } else {
            email_input_wrapper.setError("");
            FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.anim_slidein_right, R.anim.anim_slideout_left)
                    .addToBackStack(null)
                    .replace(R.id.signin_fragment_container, EmailSigninFragment.newInstance(emailinput.getText().toString())).commit();
        }

    }
}
