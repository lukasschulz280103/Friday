package com.friday.ar.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.friday.ar.R
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener
import com.friday.ar.fragments.signinFragment.DefaultSignInFragment
import com.friday.ar.fragments.wizard.AcceptTerms
import com.friday.ar.fragments.wizard.PermissionRequestFragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage


class WizardActivity : AppIntro() {

    private var onAuthCompletedListener: OnAuthCompletedListener = object : OnAuthCompletedListener {
        override fun onAuthCompleted() {
            nextButton.callOnClick()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSkipButton(false)
        showStatusBar(false)

        val welcomePage = SliderPage()
        welcomePage.title = getString(R.string.app_welcome)
        welcomePage.description = getString(R.string.wizard_welcome_descr)
        welcomePage.imageDrawable = R.mipmap.ic_launcher

        val projectInfoPage = SliderPage()
        projectInfoPage.title = getString(R.string.project_friday)
        projectInfoPage.description = getString(R.string.app_friday_description)

        val signinFragment = DefaultSignInFragment()
        signinFragment.addOnAuthCompletedListener(onAuthCompletedListener)

        addSlide(AppIntroFragment.newInstance(welcomePage))
        addSlide(AppIntroFragment.newInstance(projectInfoPage))
        addSlide(AcceptTerms.newInstance())
        addSlide(signinFragment)
        addSlide(PermissionRequestFragment())
    }

    override fun onBackPressed() {

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }
}
