package com.friday.ar.preference;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.friday.ar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class AccountPreference extends Preference {
    TextView accountname;
    TextView accountmail;
    ImageView account_provider;
    ImageView account_image;
    TextView signin;
    ConstraintLayout content;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();

    public AccountPreference(Context context) {
        super(context);
    }

    public AccountPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        account_image = (ImageView) holder.findViewById(R.id.account_img);
        account_provider = (ImageView) holder.findViewById(R.id.account_preference_provider);
        accountname = (TextView) holder.findViewById(R.id.account_pref_name);
        accountmail = (TextView) holder.findViewById(R.id.account_preference_email);
        signin = (TextView) holder.findViewById(R.id.account_preference_text_not_signed_in);
        content = (ConstraintLayout) holder.findViewById(R.id.account_preference_content);
        if (firebaseUser != null) {
            signin.setVisibility(View.GONE);
            accountname.setText(firebaseUser.getDisplayName());
            accountmail.setText(firebaseUser.getEmail());
            account_image.setImageURI(Uri.parse(getContext().getFilesDir() + "/profile/avatar.jpg"));
            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                switch (user.getProviderId()) {
                    case "google.com": {
                        account_provider.setImageResource(R.drawable.googleg_standard_color_18);
                    }
                }
            }
        } else {
            content.setVisibility(View.GONE);
        }
    }
}
