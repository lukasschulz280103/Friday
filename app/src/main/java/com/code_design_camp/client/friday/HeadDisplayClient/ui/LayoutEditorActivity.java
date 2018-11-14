package com.code_design_camp.client.friday.HeadDisplayClient.ui;

import android.os.Bundle;
import android.view.Window;

import com.code_design_camp.client.friday.HeadDisplayClient.Theme;

import androidx.appcompat.app.AppCompatActivity;

public class LayoutEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }
}
