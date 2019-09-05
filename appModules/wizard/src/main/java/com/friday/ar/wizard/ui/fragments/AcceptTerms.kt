package com.friday.ar.wizard.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.friday.ar.wizard.R
import kotlinx.android.synthetic.main.fragment_accept_terms.view.*


class AcceptTerms : CustomAppIntroFragment() {
    private lateinit var fragmentView: View
    override fun createPageContent(inflater: LayoutInflater, container: ViewGroup): View {
        fragmentView = inflater.inflate(R.layout.fragment_accept_terms, container, false)
        return fragmentView
    }

    override fun getPageTitle(): String {
        return requireContext().getString(R.string.terms_of_use)
    }

    companion object {
        fun newInstance(): AcceptTerms {
            val args = Bundle()

            val fragment = AcceptTerms()
            fragment.arguments = args
            return fragment
        }
    }

    override fun isPolicyRespected(): Boolean {
        return fragmentView.check_accept_tou.isChecked && fragmentView.check_accept_pp.isChecked
    }

    override fun onUserIllegallyRequestedNextPage() {

    }
}
