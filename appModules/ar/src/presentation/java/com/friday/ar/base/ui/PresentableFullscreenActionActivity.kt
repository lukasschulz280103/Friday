package com.friday.ar.base.ui

import android.os.Bundle
import com.friday.ar.base.R
import com.friday.ar.core.activity.FridayActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PresentableFullscreenActionActivity : FullscreenActionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MaterialAlertDialogBuilder(this)
                .setTitle("Presentation-mode")
                .setMessage("This version of friday is for presentation purposes only. Features may be limited or disabled.")
                .setIcon(R.drawable.ic_info_black_24dp)
                .setPositiveButton("Got it", null)
                .create().show()
    }
}