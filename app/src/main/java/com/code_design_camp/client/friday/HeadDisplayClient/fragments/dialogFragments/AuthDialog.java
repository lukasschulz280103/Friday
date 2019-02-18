package com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments;


<<<<<<< HEAD
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
=======
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
>>>>>>> luke
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
<<<<<<< HEAD
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.MainActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Util.FileUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import static android.content.ContentValues.TAG;


public class AuthDialog extends DialogFragment {
    private static final int RC_SIGN_IN = 9001;
    Dialog dialog;
    AlertDialog.Builder loading_dialog_builder;
    AlertDialog loading_dialog;
    TextInputEditText emailinput;
    TextInputLayout email_input_wrapper;
    AppCompatImageButton submit_form;
    SignInButton google_signin;
    GoogleSignInClient mSignInClient;
    Activity mActivity;
    View fragmentview;
    private FirebaseAuth mAuth;
    ImageButton.OnClickListener dismissdialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((MainActivity) mActivity).dismissSinginPrompt();
        }
    };
    private onAuthCompletedListener mOnAuthListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mActivity = getActivity();
=======
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
>>>>>>> luke
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
<<<<<<< HEAD
        dialog = super.onCreateDialog(savedInstanceState);
=======
        Dialog dialog = super.onCreateDialog(savedInstanceState);
>>>>>>> luke
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

<<<<<<< HEAD
    public void setOnAuthListener(onAuthCompletedListener mOnAuthListener) {
        this.mOnAuthListener = mOnAuthListener;
=======
    public OnAuthCompletedListener getOnAuthListener() {
        return signinFragment.getmOnAuthCompletedListener();
    }

    public void setOnAuthListener(OnAuthCompletedListener mOnAuthListener) {
        signinFragment.setOnAuthCompletedListener(mOnAuthListener);
>>>>>>> luke
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
<<<<<<< HEAD
        Log.d("AuthDialog","getDialog = "+getDialog());
        Log.d("AuthDialog","Creating auth dialog");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("993428557619-umbmutm9i238kmtth7vcoa4nhfedvemg.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(mActivity, gso);
        fragmentview = inflater.inflate(R.layout.signin_layout, container, false);

        ImageButton dismissbtn = fragmentview.findViewById(R.id.close_signin_dialog);
        email_input_wrapper = fragmentview.findViewById(R.id.email_input_layout);
        emailinput = fragmentview.findViewById(R.id.email_input_signin);
        submit_form = fragmentview.findViewById(R.id.submit_form);
        google_signin = fragmentview.findViewById(R.id.signin_google_button);

        google_signin.setOnClickListener(requestSignin);
        submit_form.setOnClickListener(submitBtnClick);
        emailinput.setOnEditorActionListener(mOnEditorActionListener);
        dismissbtn.setOnClickListener(dismissdialog);
        return fragmentview;
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Log.d("AuthDialog","onactivitycreated getDialog() = "+getDialog());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("AuthDialog","onstart getDialog() = "+getDialog());
    }
    AppCompatImageButton.OnClickListener submitBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            validateEmail();
        }
    };
    TextInputEditText.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if(i == EditorInfo.IME_ACTION_DONE){
                switch(textView.getId()){
                    case R.id.email_input_signin:{
                        validateEmail();
                    }
                }
                return true;
            }
            return false;
        }
    };
    SignInButton.OnClickListener requestSignin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent signInIntent = mSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };
    private void validateEmail(){
/*        String text = emailinput.getText().toString();
        if(!text.contains(".")||!text.contains("@")){
            email_input_wrapper.setError(getString(R.string.mail_invalid_error));
        }
        else{
            email_input_wrapper.setError("");
        }*/
        emailinput.setError(getString(R.string.email_signin_not_supported));
    }
/*    private void validatePassword(){

    }*/

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
                apierrrdialog.setTitle(e.getStatusCode()+ ": " + getString(R.string.internal_error_title));
                apierrrdialog.setMessage(e.getMessage());
                apierrrdialog.setPositiveButton(android.R.string.ok,null);
                apierrrdialog.create().show();

            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("CONTEXT", "Activity is " + mActivity);
                            DownloadManager downloadManager = (DownloadManager) mActivity.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            DownloadManager.Request dmrequest = new DownloadManager.Request(user.getPhotoUrl());
                            dmrequest.setVisibleInDownloadsUi(false);
                            dmrequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                            dmrequest.setDestinationInExternalFilesDir(getContext(), "profile", "/avatar.jpg");
                            mActivity.registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    loading_dialog.dismiss();
                                    File avatar_temp = new File(getActivity().getExternalFilesDir("profile"), "avatar.jpg");
                                    File avatar_dest = new File(getActivity().getFilesDir() + "/profile/", "avatar.jpg");
                                    try {
                                        FileUtil.moveFile(avatar_temp, avatar_dest);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    mOnAuthListener.onAuthCompleted();
                                }
                            }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            long downloadReference = downloadManager.enqueue(dmrequest);
                            Snackbar.make(mActivity.findViewById(R.id.main_bottom_nav), getString(R.string.signin_welcome, user.getDisplayName()), Snackbar.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(mActivity.findViewById(R.id.main_bottom_nav), "Sign in error.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public interface onAuthCompletedListener {
        void onAuthCompleted();
=======
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
>>>>>>> luke
    }
}
