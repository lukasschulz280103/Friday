package com.code_design_camp.client.friday.HeadDisplayClient.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.code_design_camp.client.friday.HeadDisplayClient.MainActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.AuthDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    FirebaseAuth fauth;
    FirebaseUser fuser;
    MainActivity mainActivity;

    Button signinButton;

    TextView emailtext;
    TextView welcometext;

    ViewSwitcher viewSwitcher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fauth = FirebaseAuth.getInstance();
        fuser = fauth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        View fragmentview = inflater.inflate(R.layout.fragment_profile, container, false);
        viewSwitcher = fragmentview.findViewById(R.id.page_profile_account_vswitcher);
        emailtext = fragmentview.findViewById(R.id.page_profile_email);
        welcometext = fragmentview.findViewById(R.id.page_profile_header);
        signinButton = fragmentview.findViewById(R.id.page_profile_signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.promptSignin();
                mainActivity.getAuthDialogFragment().setOnAuthListener(new AuthDialog.onAuthCompletedListener() {
                    @Override
                    public void onAuthCompleted() {
                        if (fuser != null) {
                            viewSwitcher.setDisplayedChild(1);
                            setupSigninScreen();
                            mainActivity.dismissSinginPrompt();
                        }
                    }
                });
            }
        });
        if (fauth.getCurrentUser() == null) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
            setupSigninScreen();
        }
        return fragmentview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fauth.getCurrentUser() == null) {
            viewSwitcher.setDisplayedChild(0);
        }
    }

    private void setupSigninScreen() {
        emailtext.setText(fuser.getEmail());
        welcometext.setText(getString(R.string.page_profile_header_text, fuser.getDisplayName()));
    }
}
