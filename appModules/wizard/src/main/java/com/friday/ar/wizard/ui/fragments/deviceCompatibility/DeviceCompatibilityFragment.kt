package com.friday.ar.wizard.ui.fragments.deviceCompatibility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.friday.ar.wizard.R
import com.friday.ar.wizard.ui.fragments.CustomAppIntroFragment
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.ArCoreApk
import kotlinx.android.synthetic.main.fragment_check_device_compatibility.view.*

class DeviceCompatibilityFragment : CustomAppIntroFragment() {
    private lateinit var fragmentView: View

    private var mIsSupported = false
    private var mIsInstalled = false


    override fun isPolicyRespected(): Boolean {
        return mIsSupported && mIsInstalled
    }

    override fun onUserIllegallyRequestedNextPage() {
        Snackbar.make(fragmentView, R.string.device_compatibility_illegal_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun createPageContent(inflater: LayoutInflater, container: ViewGroup): View {
        fragmentView = inflater.inflate(R.layout.fragment_check_device_compatibility, container, false)
        val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application).create(DeviceCompatibilityFragmentViewModel::class.java)
        viewModel.isArCoreSupported.observe(this, Observer { availability ->
            mIsSupported = availability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD || availability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED || availability == ArCoreApk.Availability.SUPPORTED_INSTALLED
            mIsInstalled = availability == ArCoreApk.Availability.SUPPORTED_INSTALLED
            val mArCoreIsUpToDate = availability != ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD
            if (mIsSupported) fragmentView.arcore_supported_indicator.setImageDrawable(requireContext().getDrawable(R.drawable.ic_check_green_24dp))
            if (mIsInstalled) fragmentView.arcore_installed_indicator.setImageDrawable(requireContext().getDrawable(R.drawable.ic_check_green_24dp))
            if (mIsInstalled && mArCoreIsUpToDate) fragmentView.arcore_up_to_date_indicator.setImageDrawable(requireContext().getDrawable(R.drawable.ic_check_green_24dp))
        })
        viewModel.checkAvailability()
        return fragmentView
    }

    override fun getPageTitle(): String {
        return requireContext().getString(R.string.device_compatibility)
    }
}