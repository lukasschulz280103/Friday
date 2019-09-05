package com.friday.ar.wizard.ui.fragments.unlocker

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friday.ar.wizard.R
import com.friday.ar.wizard.ui.fragments.CustomAppIntroFragment
import kotlinx.android.synthetic.main.fragment_unlocker.view.*

class UnlockerFragment : CustomAppIntroFragment() {
    private lateinit var fragmentView: View
    override fun isPolicyRespected(): Boolean {
        return fragmentView.codeEditText.text!!.isNotEmpty()
    }

    override fun onUserIllegallyRequestedNextPage() {
        fragmentBaseView.codeEditTextLayout.error = requireContext().getString(R.string.unlocker_illegal_error)
    }

    override fun createPageContent(inflater: LayoutInflater, container: ViewGroup): View {
        fragmentView = inflater.inflate(R.layout.fragment_unlocker, container, false)
        fragmentView.codeEditText.setOnEditorActionListener { _, i, keyEvent ->
            if (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                fragmentView.codeEditText.isEnabled = false
                fragmentView.codeEditTextLayout.helperText = "Success!"
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        return fragmentView
    }

    override fun getPageTitle(): String {
        return requireContext().getString(R.string.unlocker_title)
    }

}
