package com.friday.ar.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.friday.ar.activities.FridayActivity
import com.friday.ar.dialog.ThemeDialog
import com.friday.ar.fragments.preferenceFragments.MainSettingsFragment

class SettingsActivity : FridayActivity(), ThemeDialog.OnSelectedTheme {
    companion object {
        private const val LOGTAG = "SettingsActivity"
    }

    private var mainSettingsFragment = MainSettingsFragment()
    private var themeChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(android.R.id.content,
                mainSettingsFragment).commit()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        if (intent.hasExtra("hasChanged")) {
            themeChanged = intent.getBooleanExtra("hasChanged", false)
            setResult(RESULT_OK)
        }
    }


    override fun onSelectedTheme(hasChanged: Boolean) {
        Log.d(LOGTAG, "hasChanged:$hasChanged")
        themeChanged = hasChanged
        if (hasChanged) {
            intent.putExtra("hasChanged", true)
            recreate()
            Log.d(LOGTAG, "context:$baseContext")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(LOGTAG, "themeChanged:$themeChanged")
        if (item.itemId == android.R.id.home && themeChanged) {
            setResult(RESULT_OK)
        }
        super.onBackPressed()
        return true
    }
}
