package com.friday.ar.fragments.dialogFragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.friday.ar.R

open class FullscreenDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!, R.style.FullscreenTheme_Dialog)
    }
}