package com.code_design_camp.client.friday.HeadDisplayClient;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    Button update_btn;
    FirebaseDatabase db;
    DatabaseReference versionref;
    PackageInfo pi;

    TextView version;

    String version_number_server;
    String version_number_local;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        versionref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot", dataSnapshot.getValue().toString());
                version_number_server = (String) dataSnapshot.getValue();
                if (!version_number_server.equals(version_number_local)) {
                    update_btn.setEnabled(true);
                    update_btn.setText("Update to " + version_number_server);
                } else {
                    update_btn.setEnabled(false);
                    update_btn.setText("Up to date");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                version.setText("Could not check for updates");
            }
        });

    }
}
