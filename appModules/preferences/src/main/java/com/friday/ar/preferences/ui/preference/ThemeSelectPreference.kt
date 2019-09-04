package com.friday.ar.preferences.ui.preference

import android.content.Context
import android.util.AttributeSet

import androidx.preference.Preference

import com.friday.ar.preferences.ui.SettingsActivity
import com.friday.ar.preferences.ui.dialog.ThemeDialog

class ThemeSelectPreference(c: Context, attrs: AttributeSet) : Preference(c, attrs) {
    internal var d = ThemeDialog()

    fun showDialog(listener: ThemeDialog.OnSelectedTheme) {
        val fm = (context as SettingsActivity).supportFragmentManager
        d.setOnApplyThemeListener(listener)
        d.show(fm, "test")
    }
}
