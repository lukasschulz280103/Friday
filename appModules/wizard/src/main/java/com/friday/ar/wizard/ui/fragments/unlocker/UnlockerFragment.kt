package com.friday.ar.wizard.ui.fragments.unlocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.friday.ar.wizard.R
import com.friday.ar.wizard.ui.fragments.CustomAppIntroFragment

class UnlockerFragment : CustomAppIntroFragment() {
    override fun isPolicyRespected(): Boolean {
        return false
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

    override fun createPageContent(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.fragment_unlocker, container, false)
    }

    override fun getPageTitle(): String {
        return "Unlock your glasses"
    }
}
