package com.friday.ar.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.friday.ar.ui.mainactivity.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class PresentableMainActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Starting in presentation mode", Toast.LENGTH_SHORT).show()
    }

}