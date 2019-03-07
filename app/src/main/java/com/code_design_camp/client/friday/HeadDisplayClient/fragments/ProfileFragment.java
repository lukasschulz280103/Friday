package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.Activity;
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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.interfaces.OnAuthCompletedListener;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.FeedbackSenderActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private FirebaseAuth fauth;
    private FirebaseUser fuser;
    private MainActivity mainActivity;

    private CircularImageView account_image;
    private TextView emailtext, welcometext;

    private ViewSwitcher viewSwitcher;
    private View.OnClickListener intentmanager = view -> {
        switch (view.getId()) {
            case R.id.main_layout_editor: {
                Toast.makeText(getContext(), R.string.feature_soon, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_settings: {
                startActivityForResult(new Intent("com.friday.settings"), 0);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentview = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity) getActivity();
        viewSwitcher = fragmentview.findViewById(R.id.page_profile_account_vswitcher);
        emailtext = fragmentview.findViewById(R.id.page_profile_email);
        account_image = fragmentview.findViewById(R.id.page_profile_image_account);
        welcometext = fragmentview.findViewById(R.id.page_profile_header);
        Button signinButton = fragmentview.findViewById(R.id.page_profile_signin_button);
        LinearLayout tofeedback = fragmentview.findViewById(R.id.main_feedback);
        LinearLayout tosettings = fragmentview.findViewById(R.id.main_settings);
        LinearLayout tolayouteditor = fragmentview.findViewById(R.id.main_layout_editor);
        LinearLayout tohelp = fragmentview.findViewById(R.id.main_help);
        signinButton.setOnClickListener(view -> {
            mainActivity.promptSignin();
            mainActivity.setmOnAuthCompleted(new OnAuthCompletedListener() {
                @Override
                public void onAuthCompleted() {
                    Log.d("ONAUTHCOMPLETED", "AUTH COMPLETED");
                    fuser = fauth.getCurrentUser();
                    viewSwitcher.setDisplayedChild(1);
                    setupSigninScreen();
                    mainActivity.getAuthDialogFragment().dismissDialog();
                }

                @Override
                public void onCanceled() {

                }
            });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            mainActivity.recreate();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
