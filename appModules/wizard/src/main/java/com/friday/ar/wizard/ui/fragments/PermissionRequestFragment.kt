package com.friday.ar.wizard.ui.fragments


import android.Manifest
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import com.friday.ar.wizard.R
import kotlinx.android.synthetic.main.fragment_permission_request.view.*

class PermissionRequestFragment : CustomAppIntroFragment() {
    companion object {
        private const val PERMS_REQ_CODE = 301
    }

    internal var permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)

    private fun requestPermissions() {
        requestPermissions(permissions, PERMS_REQ_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val targetGrantResults = intArrayOf(PermissionChecker.PERMISSION_GRANTED, PermissionChecker.PERMISSION_GRANTED)
        if (requestCode == PERMS_REQ_CODE) {
            if (targetGrantResults.contentEquals(grantResults)) {
                fragmentBaseView.request_perm_btn.isEnabled = false
                fragmentBaseView.warn.setTextColor(Color.GREEN)
                fragmentBaseView.warn.setCompoundDrawablesWithIntrinsicBounds(activity!!.getDrawable(R.drawable.ic_check_green_24dp), null, null, null)
                fragmentBaseView.warn.setText(R.string.perms_granted)
            } else {
                fragmentBaseView.warn.setTextColor(context!!.getColor(R.color.warn_red))
                fragmentBaseView.warn.setCompoundDrawables(null, null, null, null)
                fragmentBaseView.warn.setText(R.string.permissions_required)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun isPolicyRespected(): Boolean {
        return PermissionChecker.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED || PermissionChecker.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

    override fun createPageContent(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.fragment_permission_request, container, false)
        v.request_perm_btn.setOnClickListener { requestPermissions() }
        return v
    }

    override fun getPageTitle(): String {
        return requireContext().getString(R.string.request_permissions)
    }
}
