package com.friday.ar.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.Util.Connectivity;
import com.friday.ar.activities.FridayActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class InfoActivity extends FridayActivity {
    Button updateBtn;
    FirebaseDatabase firebaseVersionDB;
    DatabaseReference versionRef;
    PackageInfo packageInfo;

    TextView version;

    String versionNumberServer;
    String versionNumberLocal;
    private View.OnClickListener updatePageRedirect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent redirect = new Intent();
            redirect.setAction(Intent.ACTION_VIEW);
            redirect.setData(Uri.parse("https://github.com/lukasschulz280103/Friday/releases/download/v" + versionNumberServer + "/app-release.apk"));
            startActivity(redirect);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);
        updateBtn = findViewById(R.id.info_preference_update_btn);
        version = findViewById(R.id.info_version);
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionNumberLocal = packageInfo.versionName;
            version.setText(getString(R.string.version_text, versionNumberLocal));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("InfoActivity", e.getLocalizedMessage(), e);

        }
        firebaseVersionDB = FirebaseDatabase.getInstance();
        versionRef = firebaseVersionDB.getReference("version");
        if (!Connectivity.isConnected(InfoActivity.this)) {
            updateBtn.setEnabled(false);
            updateBtn.setText(R.string.network_error_title_short);
        }
        versionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot", Objects.requireNonNull(dataSnapshot.getValue()).toString());
                versionNumberServer = (String) dataSnapshot.getValue();
                if (!versionNumberServer.equals(versionNumberLocal)) {
                    updateBtn.setEnabled(true);
                    updateBtn.setText(getString(R.string.info_update_to, versionNumberServer));
                    updateBtn.setOnClickListener(updatePageRedirect);
                } else {
                    updateBtn.setEnabled(false);
                    updateBtn.setText(R.string.info_up_to_date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                version.setText(R.string.check_update_error);
            }
        });
    }

    public void onClickLicense(View v) {
        Intent intent = new Intent(this, LicenseActivity.class);
        startActivity(intent);
    }
}
