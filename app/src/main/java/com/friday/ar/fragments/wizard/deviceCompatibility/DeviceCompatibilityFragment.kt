package com.friday.ar.fragments.wizard.deviceCompatibility

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.friday.ar.R
import com.github.paolorotolo.appintro.ISlidePolicy
import com.google.ar.core.ArCoreApk
import kotlinx.android.synthetic.main.fragment_check_device_compatibility.view.*

class DeviceCompatibilityFragment : Fragment(), ISlidePolicy {
    private var mIsSupported = false
    private var mIsInstalled = false
    private lateinit var mContext: Context


    override fun isPolicyRespected(): Boolean {
        return mIsSupported && mIsInstalled
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_check_device_compatibility, container, false)
        val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application).create(DeviceCompatibilityFragmentViewModel::class.java)
        viewModel.isArCoreSupported.observe(this, Observer { availability ->
            mIsSupported = availability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD || availability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED || availability == ArCoreApk.Availability.SUPPORTED_INSTALLED
            mIsInstalled = availability == ArCoreApk.Availability.SUPPORTED_INSTALLED
            val mArCoreIsUpToDate = availability != ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD
            if (mIsSupported) fragmentView.arcore_supported_indicator.setImageDrawable(mContext.getDrawable(R.drawable.ic_check_green_24dp))
            if (mIsInstalled) fragmentView.arcore_installed_indicator.setImageDrawable(mContext.getDrawable(R.drawable.ic_check_green_24dp))
            if (mIsInstalled && mArCoreIsUpToDate) fragmentView.arcore_up_to_date_indicator.setImageDrawable(mContext.getDrawable(R.drawable.ic_check_green_24dp))
        })
        viewModel.checkAvailability()
        return fragmentView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}