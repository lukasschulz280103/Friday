package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.MainActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.ContentValues.TAG;

public class AuthDialog extends DialogFragment {
    private static final int RC_SIGN_IN = 9001;
    Dialog dialog;
    TextInputEditText emailinput;
    TextInputLayout email_input_wrapper;
    AppCompatImageButton submit_form;
    SignInButton google_signin;
    GoogleSignInClient mSignInClient;
    View fragmentview;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("AuthDialog","getDialog = "+getDialog());
        Log.d("AuthDialog","Creating auth dialog");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("993428557619-nh2tmbaj3o4c8rppmskjg6hd6bu39md8.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(getActivity(),gso);
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

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Log.d("AuthDialog","oncreatedialog getDialog = "+getDialog());
        Log.d("AuthDialog","onCreateDialog");
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    ImageButton.OnClickListener dismissdialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((MainActivity) getActivity()).dismissSinginPrompt();
        }
    };
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
        String text = emailinput.getText().toString();
        if(!text.contains(".")||!text.contains("@")){
            email_input_wrapper.setError(getString(R.string.mail_invalid_error));
        }
        else{
            email_input_wrapper.setError("");
        }
    }
/*    private void validatePassword(){

    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                AlertDialog.Builder apierrrdialog = new AlertDialog.Builder(getContext());
                apierrrdialog.setTitle(e.getStatusCode()+ ": " + getString(R.string.internal_error_title));
                apierrrdialog.setMessage(R.string.internal_error_message);
                apierrrdialog.setPositiveButton(android.R.string.ok,null);
                apierrrdialog.create().show();

            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Snackbar.make(getActivity().findViewById(R.id.main_bottom_nav),getString(R.string.signin_welcome,user.getDisplayName()),Snackbar.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(getActivity().findViewById(R.id.main_bottom_nav),"Sign in error.",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}