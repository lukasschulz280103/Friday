package com.friday.ar.fragments.store

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat

import com.friday.ar.R
import com.friday.ar.ui.store.packageInstaller.StoreInstallationManagerActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ManagerBottomSheetDialogFragment(private val contextActivity: Activity) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView = inflater.inflate(R.layout.storemanager_bottom_sheet, container, false)
        dialogView.findViewById<View>(R.id.myInstallations).setOnClickListener { view -> startActivityAnimated(Intent(activity, StoreInstallationManagerActivity::class.java), view) }
        return dialogView
    }

    fun startActivityAnimated(intent: Intent, animationTarget: View) {
        val activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(contextActivity, animationTarget, animationTarget.transitionName)
                .toBundle()
        startActivity(intent, activityOptions)
    }
}
