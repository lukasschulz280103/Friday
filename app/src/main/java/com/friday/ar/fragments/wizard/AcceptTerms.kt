package com.friday.ar.fragments.wizard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ScrollView
import android.widget.TextView

import androidx.fragment.app.Fragment

import com.friday.ar.R
import com.github.paolorotolo.appintro.ISlidePolicy


class AcceptTerms : Fragment(), ISlidePolicy {
    private lateinit var acceptTOU: CheckBox
    internal lateinit var acceptPP: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_accept_terms, container, false)
        val tou_scroll = v.findViewById<ScrollView>(R.id.tou_scroll)
        val tou_text = v.findViewById<TextView>(R.id.tou_text)
        acceptTOU = v.findViewById(R.id.check_accept_tou)
        acceptPP = v.findViewById(R.id.check_accept_pp)
        return v
    }

    override fun isPolicyRespected(): Boolean {
        return acceptTOU.isChecked && acceptPP.isChecked
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

    companion object {

        fun newInstance(): AcceptTerms {
            val args = Bundle()

            val fragment = AcceptTerms()
            fragment.arguments = args
            return fragment
        }
    }
}
