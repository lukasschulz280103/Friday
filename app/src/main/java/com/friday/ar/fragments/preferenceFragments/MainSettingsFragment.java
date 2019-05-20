package com.friday.ar.fragments.preferenceFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.dialog.ProgressDialog;
import com.friday.ar.dialog.ThemeDialog;
import com.friday.ar.preference.ThemeSelectPreference;
import com.friday.ar.service.FeedbackService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Objects;

public class MainSettingsFragment extends PreferenceFragmentCompat {
    private static final String LOGTAG = "SettingsFragment";
    private Activity mActivity;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog loadingDialog;
    private CheckBoxPreference devModeShowChangelog;

    private ThemeDialog.OnSelectedTheme onSelectedTheme = (hasChanged) -> Log.d("SetttingsActivity", "onThemeSelected");
    private Preference.OnPreferenceClickListener themePreferenceClickListener = preference -> {
        ThemeSelectPreference pref = (ThemeSelectPreference) preference;
        pref.showDialog(onSelectedTheme);
        return true;
    };
    private Preference.OnPreferenceClickListener signOutClickListener = preference -> {
        MaterialAlertDialogBuilder confirm_signout = new MaterialAlertDialogBuilder(mActivity);
        confirm_signout.setTitle(R.string.confirm_signout_title);
        confirm_signout.setMessage(R.string.confirm_signout_message);
        confirm_signout.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            firebaseAuth.signOut();
            deleteLocalUserData();
            Toast.makeText(getActivity(), getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show();
            mActivity.finish();
        });
        confirm_signout.setNegativeButton(android.R.string.no, null);
        confirm_signout.create().show();
        return true;
    };
    private OnCompleteListener<Void> onAccountDeletionCompleteListener = new OnCompleteListener<Void>() {
        MaterialAlertDialogBuilder deletionErrorDialog;

        @Override
        public void onComplete(@NonNull Task task) {
            deletionErrorDialog = new MaterialAlertDialogBuilder(mActivity);
            loadingDialog.dismiss();
            if (task.isSuccessful()) {
                deleteLocalUserData();
                Toast.makeText(getActivity(), getString(R.string.deletion_success), Toast.LENGTH_LONG).show();
                mActivity.finish();
            } else if (task.isCanceled()) {
                deletionErrorDialog.setTitle(R.string.deletion_error_canceled_title);
                deletionErrorDialog.setMessage(R.string.deletion_error_canceled_message);
                deletionErrorDialog.setPositiveButton(android.R.string.ok, null);
                deletionErrorDialog.create().show();
            } else {
                deletionErrorDialog.setTitle(R.string.deletion_error_unknown_title);
                deletionErrorDialog.setMessage(Objects.requireNonNull(task.getException()).getMessage());
                deletionErrorDialog.setPositiveButton(android.R.string.ok, null);
                deletionErrorDialog.create().show();
                Log.e("AccountDeletion", "Could not delete account", task.getException());
            }
        }
    };
    private Preference.OnPreferenceClickListener onAccountDeletionClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            MaterialAlertDialogBuilder confirmdeletion = new MaterialAlertDialogBuilder(mActivity);
            confirmdeletion.setIcon(R.drawable.ic_warning_black_24dp);
            confirmdeletion.setTitle(R.string.confirm_deletion_title);
            confirmdeletion.setMessage(R.string.confirm_deletion_message);

            @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.deletion_dialog_feedback, null, false);
            RadioGroup rGroup = dialogView.findViewById(R.id.deletion_reason_rgroup);
            TextInputEditText attachmentText = dialogView.findViewById(R.id.reason_attached_text);

            final String[] reasonKeyword = {""};
            rGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                attachmentText.setVisibility(View.GONE);
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.reason_bad_experience: {
                        reasonKeyword[0] = "REASON_BAD_EXPERIENCE";
                    }
                    case R.id.reason_login_issues: {
                        reasonKeyword[0] = "REASON_LOGIN_ISSUES";
                    }
                    case R.id.reason_app_useless: {
                        reasonKeyword[0] = "REASON_APP_USELESS";

                    }
                    case R.id.reason_no_hardware: {
                        reasonKeyword[0] = "REASON_NO_HARDWARE";

                    }
                    case R.id.reason_other: {
                        reasonKeyword[0] = "REASON_OTHER";
                        attachmentText.setVisibility(View.VISIBLE);
                    }
                }
            });
            confirmdeletion.setView(R.layout.deletion_dialog_feedback);
            confirmdeletion.setPositiveButton(getString(R.string.confirm_deletion_positive, firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : firebaseUser.getEmail()), (dialogInterface, i) -> {
                loadingDialog = new ProgressDialog(getActivity(), getString(R.string.delete_dialog_loading_text));
                loadingDialog.show();
                JobScheduler scheduler = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                PersistableBundle extra = new PersistableBundle();
                extra.putString("reason", reasonKeyword[0]);
                extra.putString("uid", firebaseUser.getUid());
                JobInfo info = new JobInfo.Builder(FridayApplication.Jobs.JOB_FEEDBACK, new ComponentName(mActivity, FeedbackService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setBackoffCriteria(30000, JobInfo.BACKOFF_POLICY_LINEAR)
                        .setExtras(extra)
                        .build();
                assert scheduler != null;
                scheduler.schedule(info);
                Task<Void> deletiontask = firebaseUser.delete();
                deletiontask.addOnCompleteListener(onAccountDeletionCompleteListener);
            });
            confirmdeletion.setNegativeButton(android.R.string.cancel, null);
            confirmdeletion.create().show();

            return false;
        }
    };
    private Preference.OnPreferenceChangeListener autoCheckUpdateChangeListener = (preference
            , o) -> {
        Boolean isChecked = (Boolean) o;
        if (!isChecked) {
            Toast.makeText(getContext(), getString(R.string.prefs_help_manually_check_update), Toast.LENGTH_LONG).show();
        }
        return true;
    };
    private Preference.OnPreferenceChangeListener devModeSwitchChange = (preference, newValue) -> {
        Boolean val = (Boolean) newValue;
        if (val) {
            devModeShowChangelog.setEnabled(true);
        } else {
            devModeShowChangelog.setEnabled(false);
            devModeShowChangelog.setChecked(false);
        }
        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        setHasOptionsMenu(true);
        mActivity = getActivity();
        Preference accountDeletionPreference = findPreference("delete_account");
        Preference signOutPreference = findPreference("pref_sign_out");
        Preference themePreference = findPreference("dialog_theme_pref");
        Preference autoUpdatePreference = findPreference("check_update_auto");
        Preference autoSyncAccountPreference = findPreference("sync_account_auto");
        SwitchPreference devMode = findPreference("devmode");
        devModeShowChangelog = findPreference("pref_devmode_show_changelog");

        accountDeletionPreference.setOnPreferenceClickListener(onAccountDeletionClickListener);
        signOutPreference.setOnPreferenceClickListener(signOutClickListener);
        themePreference.setOnPreferenceClickListener(themePreferenceClickListener);
        autoUpdatePreference.setOnPreferenceChangeListener(autoCheckUpdateChangeListener);
        devMode.setOnPreferenceChangeListener(devModeSwitchChange);
        if (firebaseUser == null) {
            signOutPreference.setEnabled(false);
            accountDeletionPreference.setEnabled(false);
            autoSyncAccountPreference.setEnabled(false);
        } else {
            accountDeletionPreference.setLayoutResource(R.layout.account_preference_delete);
        }
        if (!devMode.isChecked()) {
            devModeShowChangelog.setEnabled(false);
        }
        Theme theme = new Theme(getActivity());
        themePreference.setSummary(theme.getNameForPos(theme.indexOf(Theme.getCurrentAppTheme(getActivity()))));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //This method has to be implemented
    }

    private void deleteLocalUserData() {
        File account_file = new File(getContext().getFilesDir(), "/profile/avatar.jpg");
        boolean isFileDeleted = account_file.delete();
        Log.d("ProfilePage", "Account image file was deleted:" + isFileDeleted);
    }
}
