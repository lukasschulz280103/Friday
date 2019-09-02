package com.friday.ar.store.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import com.friday.ar.store.R
import com.friday.ar.store.ui.storeInstallationManagerActivity.StoreInstallationManagerActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.storemanager_bottom_sheet.*


class ManagerBottomSheetDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.storemanager_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myInstallations.setOnClickListener { _view -> startActivityAnimated(Intent(requireActivity(), StoreInstallationManagerActivity::class.java), _view) }
    }

    private fun startActivityAnimated(intent: Intent, animationTarget: View) {
        val activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(requireActivity(), animationTarget, animationTarget.transitionName)
                .toBundle()
        startActivity(intent, activityOptions)
    }
}
