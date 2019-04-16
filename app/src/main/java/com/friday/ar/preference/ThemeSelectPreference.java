package com.friday.ar.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;

import com.friday.ar.dialog.ThemeDialog;
import com.friday.ar.ui.SettingsActivity;

public class ThemeSelectPreference extends Preference {
    ThemeDialog d = new ThemeDialog();

    public ThemeSelectPreference(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public void showDialog(ThemeDialog.OnSelectedTheme listener) {
        FragmentManager fm = ((SettingsActivity) getContext()).getSupportFragmentManager();
        d.setOnApplyThemeListener(listener);
        d.show(fm, "test");
    }
}
