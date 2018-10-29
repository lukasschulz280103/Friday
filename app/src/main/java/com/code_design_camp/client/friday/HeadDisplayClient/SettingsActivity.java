package com.code_design_camp.client.friday.HeadDisplayClient;

import android.os.Bundle;
import android.view.MenuItem;

import com.code_design_camp.client.friday.HeadDisplayClient.fragments.preferenceFragments.MainSettingsFragment;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainSettingsFragment()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default: {
                return false;
            }
        }
    }

}
