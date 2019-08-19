package com.friday.ar.wizard.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.friday.ar.auth.fragments.DefaultSignInFragment
import com.friday.ar.auth.interfaces.OnAuthCompletedListener
import com.friday.ar.wizard.R
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
        welcomePage.description = getString(R.string.welcome_descriptioon)

        val projectInfoPage = SliderPage()
        projectInfoPage.title = getString(R.string.project_friday)

        val signinFragment = DefaultSignInFragment()
        signinFragment.addOnAuthCompletedListener(onAuthCompletedListener)

        addSlide(AppIntroFragment.newInstance(welcomePage))
        //addSlide(DeviceCompatibilityFragment())
        addSlide(AppIntroFragment.newInstance(projectInfoPage))
        //addSlide(AcceptTerms.newInstance())
        addSlide(signinFragment)
        //addSlide(PermissionRequestFragment())
    }

    override fun onBackPressed() {

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isFirstUse", false).apply()
        finish()
    }
}
