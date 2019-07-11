package com.friday.ar.fragments.preferenceFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.Theme
import com.friday.ar.dialog.ProgressDialog
import com.friday.ar.dialog.ThemeDialog
import com.friday.ar.preference.ThemeSelectPreference
import com.friday.ar.service.FeedbackService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.util.*

class MainSettingsFragment : PreferenceFragmentCompat() {

    private var mActivity: Activity? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private var loadingDialog: ProgressDialog? = null
    private var devModeShowChangelog: CheckBoxPreference? = null

    private val onSelectedTheme = object : ThemeDialog.OnSelectedTheme {
        override fun onSelectedTheme(hasChanged: Boolean) {
            Log.d("SetttingsActivity", "onThemeSelected")
        }
    }
    private val themePreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
        val pref = preference as ThemeSelectPreference
        pref.showDialog(onSelectedTheme)
        true
    }
    private val signOutClickListener = Preference.OnPreferenceClickListener {
        val confirmSignoutDialog = MaterialAlertDialogBuilder(mActivity!!)
        confirmSignoutDialog.setTitle(R.string.confirm_signout_title)
        confirmSignoutDialog.setMessage(R.string.confirm_signout_message)
        confirmSignoutDialog.setPositiveButton(android.R.string.yes) { _, _ ->
            firebaseAuth.signOut()
            deleteLocalUserData()
            Toast.makeText(activity, getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show()
            mActivity!!.finish()
        }
        confirmSignoutDialog.setNegativeButton(android.R.string.no, null)
        confirmSignoutDialog.create().show()
        true
    }
    private val onAccountDeletionCompleteListener = OnCompleteListener<Void> { task ->
        val deletionErrorDialog = MaterialAlertDialogBuilder(mActivity!!)
        loadingDialog!!.dismiss()
        if (task.isSuccessful) {
            deleteLocalUserData()
            Toast.makeText(activity, getString(R.string.deletion_success), Toast.LENGTH_LONG).show()
            mActivity!!.finish()
        } else if (task.isCanceled) {
            deletionErrorDialog.setTitle(R.string.deletion_error_canceled_title)
            deletionErrorDialog.setMessage(R.string.deletion_error_canceled_message)
            deletionErrorDialog.setPositiveButton(android.R.string.ok, null)
            deletionErrorDialog.create().show()
        } else {
            deletionErrorDialog.setTitle(R.string.deletion_error_unknown_title)
            deletionErrorDialog.setMessage(Objects.requireNonNull<Exception>(task.exception).message)
            deletionErrorDialog.setPositiveButton(android.R.string.ok, null)
            deletionErrorDialog.create().show()
            Log.e("AccountDeletion", "Could not delete account", task.exception)
        }
    }
    private val onAccountDeletionClickListener = Preference.OnPreferenceClickListener {
        val confirmDeletionDialog = MaterialAlertDialogBuilder(mActivity!!)
        confirmDeletionDialog.setIcon(R.drawable.ic_warning_black_24dp)
        confirmDeletionDialog.setTitle(R.string.confirm_deletion_title)
        confirmDeletionDialog.setMessage(R.string.confirm_deletion_message)

        @SuppressLint("InflateParams") val dialogView = layoutInflater.inflate(R.layout.deletion_dialog_feedback, null, false)
        val rGroup = dialogView.findViewById<RadioGroup>(R.id.deletion_reason_rgroup)
        val attachmentText = dialogView.findViewById<TextInputEditText>(R.id.reason_attached_text)

        val reasonKeyword = arrayOf("")
        rGroup.setOnCheckedChangeListener { radioGroup, i ->
            attachmentText.visibility = View.GONE
            when (radioGroup.checkedRadioButtonId) {
                R.id.reason_bad_experience -> {
                    run { reasonKeyword[0] = "REASON_BAD_EXPERIENCE" }
                    run { reasonKeyword[0] = "REASON_LOGIN_ISSUES" }
                    run {
                        reasonKeyword[0] = "REASON_APP_USELESS"

                    }
                    run {
                        reasonKeyword[0] = "REASON_NO_HARDWARE"

                    }
                    run {
                        reasonKeyword[0] = "REASON_OTHER"
                        attachmentText.visibility = View.VISIBLE
                    }
                }
                R.id.reason_login_issues -> {
                    run { reasonKeyword[0] = "REASON_LOGIN_ISSUES" }
                    run { reasonKeyword[0] = "REASON_APP_USELESS" }
                    run { reasonKeyword[0] = "REASON_NO_HARDWARE" }
                    run {
                        reasonKeyword[0] = "REASON_OTHER"
                        attachmentText.visibility = View.VISIBLE
                    }
                }
                R.id.reason_app_useless -> {
                    run { reasonKeyword[0] = "REASON_APP_USELESS" }
                    run { reasonKeyword[0] = "REASON_NO_HARDWARE" }
                    run {
                        reasonKeyword[0] = "REASON_OTHER"
                        attachmentText.visibility = View.VISIBLE
                    }
                }
                R.id.reason_no_hardware -> {
                    run { reasonKeyword[0] = "REASON_NO_HARDWARE" }
                    run {
                        reasonKeyword[0] = "REASON_OTHER"
                        attachmentText.visibility = View.VISIBLE
                    }
                }
                R.id.reason_other -> {
                    reasonKeyword[0] = "REASON_OTHER"
                    attachmentText.visibility = View.VISIBLE
                }
            }
        }
        confirmDeletionDialog.setView(R.layout.deletion_dialog_feedback)
        confirmDeletionDialog.setPositiveButton(getString(R.string.confirm_deletion_positive, if (firebaseUser!!.displayName != null) firebaseUser.displayName else firebaseUser.email)) { dialogInterface, i ->
            loadingDialog = ProgressDialog(activity!!, getString(R.string.delete_dialog_loading_text))
            loadingDialog!!.show()
            val scheduler = activity!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val extra = PersistableBundle()
            extra.putString("reason", reasonKeyword[0])
            extra.putString("uid", firebaseUser.uid)
            val info = JobInfo.Builder(FridayApplication.Jobs.JOB_FEEDBACK, ComponentName(mActivity!!, FeedbackService::class.java))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(30000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setExtras(extra)
                    .build()
            scheduler.schedule(info)
            val deletionTask = firebaseUser.delete()
            deletionTask.addOnCompleteListener(onAccountDeletionCompleteListener)
        }
        confirmDeletionDialog.setNegativeButton(android.R.string.cancel, null)
        confirmDeletionDialog.create().show()
        true
    }
    private val autoCheckUpdateChangeListener: Preference.OnPreferenceChangeListener? = Preference.OnPreferenceChangeListener { _, o ->
        val isChecked = o as Boolean
        if (!isChecked) {
            Toast.makeText(context, getString(R.string.prefs_help_manually_check_update), Toast.LENGTH_LONG).show()
        }
        true
    }
    private val devModeSwitchChange: Preference.OnPreferenceChangeListener? = Preference.OnPreferenceChangeListener { _, newValue ->
        val `val` = newValue as Boolean
        if (`val`) {
            devModeShowChangelog!!.isEnabled = true
        } else {
            devModeShowChangelog!!.isEnabled = false
            devModeShowChangelog!!.isChecked = false
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_main)
        setHasOptionsMenu(true)
        mActivity = activity
        val accountDeletionPreference = findPreference<Preference>("delete_account")
        val signOutPreference = findPreference<Preference>("pref_sign_out")
        val themePreference = findPreference<Preference>("dialog_theme_pref")
        val autoUpdatePreference = findPreference<Preference>("check_update_auto")
        val autoSyncAccountPreference = findPreference<Preference>("sync_account_auto")
        val devMode = findPreference<SwitchPreference>("devmode")
        devModeShowChangelog = findPreference("pref_devmode_show_changelog")

        accountDeletionPreference!!.onPreferenceClickListener = onAccountDeletionClickListener
        signOutPreference!!.onPreferenceClickListener = signOutClickListener
        themePreference!!.onPreferenceClickListener = themePreferenceClickListener
        autoUpdatePreference!!.onPreferenceChangeListener = autoCheckUpdateChangeListener
        devMode!!.onPreferenceChangeListener = devModeSwitchChange
        if (firebaseUser == null) {
            signOutPreference.isEnabled = false
            accountDeletionPreference.isEnabled = false
            autoSyncAccountPreference!!.isEnabled = false
        } else {
            accountDeletionPreference.layoutResource = R.layout.account_preference_delete
        }
        if (!devMode.isChecked) {
            devModeShowChangelog!!.isEnabled = false
        }
        val theme = Theme(mActivity!!)
        themePreference.summary = theme.getNameForPos(theme.indexOf(Theme.getCurrentAppTheme(mActivity!!)))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //This method has to be implemented
    }

    private fun deleteLocalUserData() {
        val account_file = File(context!!.filesDir, "/profile/avatar.jpg")
        val isFileDeleted = account_file.delete()
        Log.d("ProfilePage", "Account image file was deleted:$isFileDeleted")
    }

    companion object {
        private val LOGTAG = "SettingsFragment"
    }
}