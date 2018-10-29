package com.code_design_camp.client.friday.HeadDisplayClient.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.code_design_camp.client.friday.HeadDisplayClient.FeedbackSenderActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.LayoutEditorActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.MainActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.SettingsActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.AuthDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    FirebaseAuth fauth;
    FirebaseUser fuser;
    MainActivity mainActivity;

    Button signinButton;

    CircularImageView account_image;
    TextView emailtext;
    TextView welcometext;

    ViewSwitcher viewSwitcher;
    private LinearLayout tolayouteditor;
    private LinearLayout tosettings;
    private LinearLayout tohelp;
    private LinearLayout tofeedback;
    private View.OnClickListener intentmanager = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_layout_editor: {
                    startActivity(new Intent(getActivity(), LayoutEditorActivity.class));
                    break;
                }
                case R.id.main_settings: {
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    break;
                }
                case R.id.main_help: {
                    break;
                }
                case R.id.main_feedback: {
                    startActivity(new Intent(getActivity(), FeedbackSenderActivity.class));
                    break;
                }
            }
        }
    };

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
        View fragmentview = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity) getActivity();
        viewSwitcher = fragmentview.findViewById(R.id.page_profile_account_vswitcher);
        emailtext = fragmentview.findViewById(R.id.page_profile_email);
        account_image = fragmentview.findViewById(R.id.page_profile_image_account);
        welcometext = fragmentview.findViewById(R.id.page_profile_header);
        signinButton = fragmentview.findViewById(R.id.page_profile_signin_button);
        tofeedback = fragmentview.findViewById(R.id.main_feedback);
        tosettings = fragmentview.findViewById(R.id.main_settings);
        tolayouteditor = fragmentview.findViewById(R.id.main_layout_editor);
        tohelp = fragmentview.findViewById(R.id.main_help);
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
        tolayouteditor.setOnClickListener(intentmanager);
        tosettings.setOnClickListener(intentmanager);
        tohelp.setOnClickListener(intentmanager);
        tofeedback.setOnClickListener(intentmanager);
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
        try {
            Uri account_image_uri = Uri.parse("file://" + getContext().getFilesDir() + "/profile/avatar.jpg");
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), account_image_uri);
            Palette p = Palette.from(bm).generate();
            account_image.setBorderColor(p.getDominantColor(Color.GRAY));
            account_image.setImageURI(account_image_uri);
            emailtext.setText(fuser.getEmail());
            welcometext.setText(getString(R.string.page_profile_header_text, fuser.getDisplayName()));
        } catch (IOException e) {
            Log.e("ProfilePage", e.getLocalizedMessage(), e);
        }
    }
}
