package com.friday.ar.wizard.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.friday.ar.auth.fragments.DefaultSignInFragment
import com.friday.ar.auth.interfaces.OnAuthCompletedListener
import com.friday.ar.wizard.R
import com.friday.ar.wizard.ui.fragments.AcceptTerms
import com.friday.ar.wizard.ui.fragments.PermissionRequestFragment
import com.friday.ar.wizard.ui.fragments.deviceCompatibility.DeviceCompatibilityFragment
import com.friday.ar.wizard.ui.fragments.unlocker.UnlockerFragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import org.koin.android.ext.android.get


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

        val signinFragment = DefaultSignInFragment()
        signinFragment.addOnAuthCompletedListener(onAuthCompletedListener)

        addSlide(AppIntroFragment.newInstance(welcomePage))
        addSlide(DeviceCompatibilityFragment())
        addSlide(AcceptTerms.newInstance())
        addSlide(UnlockerFragment())
        addSlide(signinFragment)
        addSlide(PermissionRequestFragment())
    }

    override fun onBackPressed() {

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        get<SharedPreferences>().edit().putBoolean("isFirstUse", false).apply()
        finish()
    }
}
