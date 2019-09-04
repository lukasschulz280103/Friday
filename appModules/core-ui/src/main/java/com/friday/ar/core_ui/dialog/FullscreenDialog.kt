package com.friday.ar.core_ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.friday.ar.core_ui.R

open class FullscreenDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.FullscreenTheme_Dialog)
    }
}