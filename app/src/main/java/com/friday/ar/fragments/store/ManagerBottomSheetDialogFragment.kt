package com.friday.ar.fragments.store

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat

import com.friday.ar.R
import com.friday.ar.ui.store.storeInstallationManagerActivity.StoreInstallationManagerActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ManagerBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var mActivity: AppCompatActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView = inflater.inflate(R.layout.storemanager_bottom_sheet, container, false)
        dialogView.findViewById<View>(R.id.myInstallations).setOnClickListener { view -> startActivityAnimated(Intent(mActivity, StoreInstallationManagerActivity::class.java), view) }
        return dialogView
    }

    private fun startActivityAnimated(intent: Intent, animationTarget: View) {
        val activityOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(mActivity, animationTarget, animationTarget.transitionName)
                .toBundle()
        startActivity(intent, activityOptions)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }
}
