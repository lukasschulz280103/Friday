package com.code_design_camp.client.friday.HeadDisplayClient.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
    }

    public void openLibraryPage(View v) {
        String url = (String) v.getTag();
        Intent openUrl = new Intent();
        openUrl.setAction(Intent.ACTION_VIEW);
        openUrl.setData(Uri.parse(url));
        startActivity(openUrl);
    }
}
