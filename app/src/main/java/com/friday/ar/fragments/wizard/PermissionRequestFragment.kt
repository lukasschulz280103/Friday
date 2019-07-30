package com.friday.ar.fragments.wizard


import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.friday.ar.R
import com.github.paolorotolo.appintro.ISlidePolicy


/**
 * A simple [Fragment] subclass.
 */
class PermissionRequestFragment : Fragment(), ISlidePolicy {
    companion object {
        private const val PERMS_REQ_CODE = 301
    }

    internal var permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
    private lateinit var requestPermissionsButton: Button
    private lateinit var warning: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_permission_request, container, false)
        requestPermissionsButton = v.findViewById(R.id.request_perm_btn)
        warning = v.findViewById(R.id.warn)
        requestPermissionsButton.setOnClickListener { requestPermissions() }
        return v
    }

    private fun requestPermissions() {
        requestPermissions(permissions, PERMS_REQ_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val targetGrantResults = intArrayOf(PermissionChecker.PERMISSION_GRANTED, PermissionChecker.PERMISSION_GRANTED)
        if (requestCode == PERMS_REQ_CODE) {
            if (targetGrantResults.contentEquals(grantResults)) {
                requestPermissionsButton.isEnabled = false
                warning.setTextColor(Color.GREEN)
                warning.setCompoundDrawablesWithIntrinsicBounds(activity!!.getDrawable(R.drawable.ic_check_green_24dp), null, null, null)
                warning.setText(R.string.perms_granted)
            } else {
                warning.setTextColor(context!!.getColor(R.color.warn_red))
                warning.setCompoundDrawables(null, null, null, null)
                warning.setText(R.string.permissions_required)
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

}
