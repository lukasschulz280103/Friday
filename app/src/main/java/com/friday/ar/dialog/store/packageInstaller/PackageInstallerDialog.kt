package com.friday.ar.dialog.store.packageInstaller

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import com.friday.ar.R
import com.friday.ar.databinding.PackageInstallerDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.package_installer_dialog.view.*
import java.io.File

/**
 * This dialog shows up when an external file browser app sends an intent to this app.
 */
class PackageInstallerDialog : BottomSheetDialogFragment() {
    private lateinit var chosenFile: File
    companion object {
        internal const val LOGTAG = "PackageInstallerDialog"
        internal const val SITE_VIEW_PLUGIN = 0
        internal const val SITE_INSTALL_PROGRESS = 1
        internal const val SITE_ERROR = 2
        fun getInstance(chosenFilePath: String): PackageInstallerDialog {
            val bundle = Bundle()
            bundle.putString("filePath", chosenFilePath)
            val packageInstallerDialog = PackageInstallerDialog()
            packageInstallerDialog.arguments = bundle
            return packageInstallerDialog
        }
    }

    private lateinit var dialogView: View
    private lateinit var mContext: Context
    private lateinit var viewModel: PackageInstallerDialogViewModel

    //TODO:outsource work to the viewmodel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments!!.getString("filePath", "").isEmpty()) throw IllegalArgumentException("Cannot open file dialog without a passed file argument.")
        chosenFile = File(arguments!!.getString("filePath")!!)
        val dataBinding = PackageInstallerDialogBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = this
        viewModel = PackageInstallerDialogViewModel.Factory(chosenFile, activity!!.application).create(PackageInstallerDialogViewModel::class.java)
        dataBinding.progressText = viewModel.installProgressMessage
        dataBinding.errorMessage = viewModel.errorMessage
        dataBinding.manifest = viewModel.manifest

        dialogView = dataBinding.root
        dialogView.view_animator.outAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_slideout_left)
        dialogView.view_animator.inAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_slidein_right)
        dialogView.view_animator.displayedChild = SITE_INSTALL_PROGRESS
        dialogView.errorOk.setOnClickListener { dismiss() }
        dialogView.pluginInstaller_activity_cancel.setOnClickListener { dismiss() }
        dialogView.startInstallation.setOnClickListener { viewModel.doInstall() }
        viewModel.displayedSite.observe(this, Observer { site -> dialogView.view_animator.displayedChild = site })
        viewModel.isProgressShown.observe(this, Observer { isShown ->
            dialogView.installProgressbar.visibility = if (isShown) View.VISIBLE else View.GONE
            dialogView.installProgressIcon.visibility = if (isShown) View.GONE else View.VISIBLE
        })
        viewModel.progressIcon.observe(this, Observer { drawableRes -> dialogView.installProgressIcon.setImageDrawable(mContext.getDrawable(drawableRes)) })
        return dialogView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity!!.finishAndRemoveTask()
    }

    fun onBackPressed(): Boolean {
        return false
    }
}
