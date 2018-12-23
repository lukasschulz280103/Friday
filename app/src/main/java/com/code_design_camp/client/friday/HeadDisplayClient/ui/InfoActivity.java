package com.code_design_camp.client.friday.HeadDisplayClient.ui;

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

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;
import com.code_design_camp.client.friday.HeadDisplayClient.Util.Connectivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InfoActivity extends FridayActivity {
    Button update_btn;
    FirebaseDatabase db;
    DatabaseReference versionref;
    PackageInfo pi;

    TextView version;

    String version_number_server;
    String version_number_local;
    private View.OnClickListener updatePageRedirect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent redirect = new Intent();
            redirect.setAction(Intent.ACTION_VIEW);
            redirect.setData(Uri.parse("https://github.com/lukasschulz280103/Friday/releases/download/v" + version_number_server + "/app-release-stable.apk"));
            startActivity(redirect);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);
        update_btn = findViewById(R.id.info_preference_update_btn);
        version = findViewById(R.id.info_version);
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            version_number_local = pi.versionName;
            version.setText(getString(R.string.version_text, version_number_local));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("InfoActivity", e.getLocalizedMessage(), e);

        }
        db = FirebaseDatabase.getInstance();
        versionref = db.getReference("version");
        if (!Connectivity.isConnected(InfoActivity.this)) {
            update_btn.setEnabled(false);
            update_btn.setText(R.string.network_error_title_short);
        }
        versionref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot", Objects.requireNonNull(dataSnapshot.getValue()).toString());
                version_number_server = (String) dataSnapshot.getValue();
                if (!version_number_server.equals(version_number_local)) {
                    update_btn.setEnabled(true);
                    update_btn.setText(getString(R.string.info_update_to, version_number_server));
                    update_btn.setOnClickListener(updatePageRedirect);
                } else {
                    update_btn.setEnabled(false);
                    update_btn.setText(R.string.info_up_to_date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                version.setText(R.string.check_update_error);
            }
        });

    }
}
