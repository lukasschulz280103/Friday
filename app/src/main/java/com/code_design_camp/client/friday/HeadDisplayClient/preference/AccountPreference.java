package com.code_design_camp.client.friday.HeadDisplayClient.preference;

import android.content.Context;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

public class AccountPreference extends Preference {
    View v;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();

    public AccountPreference(Context context) {
        super(context);
    }

    public AccountPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        v = super.getView(convertView, parent);
        AppCompatButton signin = v.findViewById(R.id.signin_button_preference);
        ConstraintLayout content = v.findViewById(R.id.account_preference_content);
        if (firebaseUser != null) {
            signin.setVisibility(View.GONE);
            ImageView account_image = v.findViewById(R.id.account_img);
            ImageView account_provider = v.findViewById(R.id.account_preference_provider);
            TextView accountname = v.findViewById(R.id.account_pref_name);
            TextView accountmail = v.findViewById(R.id.account_preference_email);
            accountname.setText(firebaseUser.getDisplayName());
            accountmail.setText(firebaseUser.getEmail());
            account_image.setImageURI(Uri.parse(getContext().getExternalFilesDir("profile") + "/avatar.jpg"));
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
        return v;
    }
}
