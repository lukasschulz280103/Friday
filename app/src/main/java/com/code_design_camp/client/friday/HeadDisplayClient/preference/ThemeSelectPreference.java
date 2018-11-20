package com.code_design_camp.client.friday.HeadDisplayClient.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;

import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ThemeDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.SettingsActivity;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;

public class ThemeSelectPreference extends Preference {
    ThemeDialog d = new ThemeDialog();

    public ThemeSelectPreference(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public void showDialog(ThemeDialog.OnSelectedTheme listener) {
        FragmentManager fm = ((SettingsActivity) ((ContextThemeWrapper) getContext()).getBaseContext()).getSupportFragmentManager();
        d.setOnApplyThemeListener(listener);
        d.show(fm, "test");
    }
}
