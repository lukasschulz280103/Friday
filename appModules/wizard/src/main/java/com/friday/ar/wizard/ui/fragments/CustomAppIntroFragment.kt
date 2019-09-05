package com.friday.ar.wizard.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.friday.ar.wizard.R
import com.github.paolorotolo.appintro.ISlidePolicy
import kotlinx.android.synthetic.main.friday_base_appintro_fragment.view.*

abstract class CustomAppIntroFragment : Fragment(), ISlidePolicy {
    internal lateinit var fragmentBaseView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBaseView = inflater.inflate(R.layout.friday_base_appintro_fragment, container, false)
        fragmentBaseView.pageContent.addView(createPageContent(inflater, fragmentBaseView as ViewGroup))
        fragmentBaseView.pageTitle.text = getPageTitle()
        return fragmentBaseView
    }

    abstract fun createPageContent(inflater: LayoutInflater, container: ViewGroup): View
    abstract fun getPageTitle(): String
}