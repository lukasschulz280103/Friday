package com.friday.ar.fragments.dialogFragments.changelog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.friday.ar.R
import kotlinx.android.synthetic.main.fragment_changelog_dialog.*
import org.koin.android.viewmodel.ext.android.viewModel

class ChangelogDialogFragment : DialogFragment() {
    companion object {
        private const val LOGTAG = "ChangeLogDialog"
        internal const val NO_UPDATE_INFO_PROVIDED = "NO_UPDATE_PROVIDED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_changelog_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: ChangeLogDialogViewModel by viewModel()

        viewModel.getUpdateTitle().observe(this, Observer { title ->
            chld_infocontainer.visibility = View.VISIBLE
            changelog_dialog_progress.visibility = View.GONE
            changelog_dialog_title.text = title
        })
        viewModel.getUpdateTimeStamp().observe(this, Observer { timestamp ->
            changelog_dialog_release_date.text = timestamp
        })
        viewModel.getUpdateBody().observe(this, Observer { bodyHtmlString ->
            changelog_dialog_body.text = bodyHtmlString
        })
        viewModel.getExceptionMessage().observe(this, Observer { message ->
            if (message == NO_UPDATE_INFO_PROVIDED) {
                Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
                dismissAllowingStateLoss()
                return@Observer
            }
            chld_error.visibility = View.VISIBLE
            changelog_dialog_progress.visibility = View.GONE
            chld_error.text = String.format("Couldn't load info. Caused by: %s", message)
            isCancelable = true
        })
        chld_dismissbtn.setOnClickListener { dismiss() }
        chld_dismissbtn.isEnabled = false
    }
}
