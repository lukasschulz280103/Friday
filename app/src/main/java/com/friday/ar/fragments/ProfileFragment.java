package com.friday.ar.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener;
import com.friday.ar.service.OnAccountSyncStateChanged;
import com.friday.ar.ui.FeedbackSenderActivity;
import com.friday.ar.ui.MainActivity;
import com.friday.ar.util.UserUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements OnAccountSyncStateChanged {
    public static final String LOGTAG = "ProfileFragment";
    private static final int REQUEST_CODE_SETTINGS = 200;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private MainActivity mainActivity;

    private CircularImageView accountImageView;
    private TextView emailText, welcomeText;

    private ViewSwitcher viewSwitcher;
    private View.OnClickListener intentManager = view -> {
        switch (view.getId()) {
            case R.id.main_layout_editor: {
                Toast.makeText(getContext(), R.string.feature_soon, Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_settings: {
                startActivityForResult(new Intent("com.friday.settings"), REQUEST_CODE_SETTINGS);
                break;
            }
            case R.id.main_help: {
                Toast.makeText(getContext(), R.string.feature_soon, Toast.LENGTH_SHORT).show();
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
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity) getActivity();
        viewSwitcher = fragmentView.findViewById(R.id.page_profile_account_vswitcher);
        emailText = fragmentView.findViewById(R.id.page_profile_email);
        accountImageView = fragmentView.findViewById(R.id.page_profile_image_account);
        welcomeText = fragmentView.findViewById(R.id.page_profile_header);
        ((FridayApplication) mainActivity.getApplication()).registerForSyncStateChange(this);
        Button signInButton = fragmentView.findViewById(R.id.page_profile_signin_button);
        LinearLayout toFeedback = fragmentView.findViewById(R.id.main_feedback);
        LinearLayout toSettings = fragmentView.findViewById(R.id.main_settings);
        LinearLayout toLayoutEditor = fragmentView.findViewById(R.id.main_layout_editor);
        LinearLayout toHelp = fragmentView.findViewById(R.id.main_help);
        signInButton.setOnClickListener(view -> {
            mainActivity.promptSignin();
            mainActivity.setmOnAuthCompleted(new OnAuthCompletedListener() {
                @Override
                public void onAuthCompleted() {
                    Log.d("ONAUTHCOMPLETED", "AUTH COMPLETED");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    viewSwitcher.setDisplayedChild(1);
                    setupSignInScreen();
                    mainActivity.getAuthDialogFragment().dismissDialog();
                }

                @Override
                public void onCanceled() {

                }
            });
        });
        if (firebaseAuth.getCurrentUser() == null) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
            setupSignInScreen();
        }
        toLayoutEditor.setOnClickListener(intentManager);
        toSettings.setOnClickListener(intentManager);
        toHelp.setOnClickListener(intentManager);
        toFeedback.setOnClickListener(intentManager);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firebaseAuth.getCurrentUser() == null) {
            viewSwitcher.setDisplayedChild(0);
        }
    }

    private void setupSignInScreen() {
        UserUtil userUtil = new UserUtil(mainActivity);
        if (firebaseUser.getPhotoUrl() != null && userUtil.getAvatarFile().exists()) {
            Uri account_image_uri = Uri.parse("file://" + getContext().getFilesDir() + "/profile/avatar.jpg");
            accountImageView.setImageURI(account_image_uri);
        } else {
            accountImageView.setBackground(mainActivity.getDrawable(R.drawable.ic_twotone_account_circle_24px));
        }
        emailText.setText(firebaseUser.getEmail());
        welcomeText.setText(firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().equals("") ? getString(R.string.page_profile_header_text, firebaseUser.getDisplayName()) : getString(R.string.greet_no_name));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(LOGTAG, "onActivityResult:[requestCode=" + requestCode + "|resultCode=" + resultCode + "|data=" + data);
        if (requestCode == REQUEST_CODE_SETTINGS && resultCode == Activity.RESULT_OK) {
            mainActivity.recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSyncStateChanged() {
        setupSignInScreen();
    }
}
