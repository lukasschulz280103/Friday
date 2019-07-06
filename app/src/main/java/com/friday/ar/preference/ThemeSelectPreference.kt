package com.friday.ar.preference

import android.content.Context
import android.util.AttributeSet

import androidx.preference.Preference

import com.friday.ar.dialog.ThemeDialog
import com.friday.ar.ui.SettingsActivity

class ThemeSelectPreference(c: Context, attrs: AttributeSet) : Preference(c, attrs) {
    internal var d = ThemeDialog()

    fun showDialog(listener: ThemeDialog.OnSelectedTheme) {
        val fm = (context as SettingsActivity).supportFragmentManager
        d.setOnApplyThemeListener(listener)
        d.show(fm, "test")
    }
}
