package com.friday.ar.fragments.dialogFragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.friday.ar.R
import com.friday.ar.ui.mainactivity.MainActivity
import com.google.android.material.button.MaterialButton

class UninstallOldAppDialog : DialogFragment() {
    private var retry: MaterialButton? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentLayout = inflater.inflate(R.layout.uninstall_old_dialog, container, false)
        retry = contentLayout.findViewById(R.id.retry_uninstallation)
        retry!!.setOnClickListener { promptUninstall() }
        return contentLayout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        promptUninstall()
    }

    private fun promptUninstall() {
        val unins = Intent(Intent.ACTION_DELETE)
        unins.data = Uri.parse("package:com.code_design_camp.client.rasberrypie.rbpieclient")
        unins.putExtra(Intent.EXTRA_RETURN_RESULT, true)
        startActivityForResult(unins, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                (activity as MainActivity).dismissUninstallPrompt()
            }
        }
    }
}
