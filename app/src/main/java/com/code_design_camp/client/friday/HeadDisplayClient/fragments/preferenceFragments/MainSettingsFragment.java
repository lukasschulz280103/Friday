package com.code_design_camp.client.friday.HeadDisplayClient.fragments.preferenceFragments;

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
import androidx.appcompat.app.AlertDialog;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.code_design_camp.client.friday.HeadDisplayClient.FridayApplication;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ProgressDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ThemeDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.preference.ThemeSelectPreference;
import com.code_design_camp.client.friday.HeadDisplayClient.service.FeedbackService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MainSettingsFragment extends PreferenceFragmentCompat {
    private static final String LOGTAG = "SettingsFragment";
    private Activity mActivity;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog loadingdialog;
    private CheckBoxPreference devmode_show_changelog;

    private ThemeDialog.OnSelectedTheme themeSelected = (t, r) -> {
        Log.d("SetttingsActivity", "onThemeSelected");
    };
    private Preference.OnPreferenceClickListener select_theme_pref_click = preference -> {
        ThemeSelectPreference pref = (ThemeSelectPreference) preference;
        pref.showDialog(themeSelected);
        return true;
    };
    private Preference.OnPreferenceClickListener sign_out_click = preference -> {
        AlertDialog.Builder confirm_signout = new AlertDialog.Builder(mActivity);
        confirm_signout.setTitle(R.string.confirm_signout_title);
        confirm_signout.setMessage(R.string.confirm_signout_message);
        confirm_signout.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            auth.signOut();
            deleteLocalUserData();
            Toast.makeText(getActivity(), getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show();
            mActivity.finish();
        });
        confirm_signout.setNegativeButton(android.R.string.no, null);
        confirm_signout.create().show();
        return true;
    };
    private OnCompleteListener deletioncallback = new OnCompleteListener() {
        AlertDialog.Builder faildeletedialog;

        @Override
        public void onComplete(@NonNull Task task) {
            faildeletedialog = new AlertDialog.Builder(mActivity);
            loadingdialog.dismiss();
            if (task.isSuccessful()) {
                deleteLocalUserData();
                Toast.makeText(getActivity(), getString(R.string.deletion_success), Toast.LENGTH_LONG).show();
                mActivity.finish();
            } else if (task.isCanceled()) {
                faildeletedialog.setTitle(R.string.deletion_error_canceled_title);
                faildeletedialog.setMessage(R.string.deletion_error_canceled_message);
                faildeletedialog.setPositiveButton(android.R.string.ok, null);
                faildeletedialog.create().show();
            } else {
                faildeletedialog.setTitle(R.string.deletion_error_unknown_title);
                faildeletedialog.setMessage(Objects.requireNonNull(task.getException()).getMessage());
                faildeletedialog.setPositiveButton(android.R.string.ok, null);
                faildeletedialog.create().show();
                Log.e("AccountDeletion", "Could not delete account", task.getException());
            }
        }
    };
    private Preference.OnPreferenceClickListener deletionlistener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder confirmdeletion = new AlertDialog.Builder(mActivity);
            confirmdeletion.setIcon(R.drawable.ic_warning_black_24dp);
            confirmdeletion.setTitle(R.string.confirm_deletion_title);
            confirmdeletion.setMessage(R.string.confirm_deletion_message);
            View dialogView = getLayoutInflater().inflate(R.layout.deletion_dialog_feedback, null, false);
            RadioGroup rGroup = dialogView.findViewById(R.id.deletion_reason_rgroup);
            TextInputEditText attachmentText = dialogView.findViewById(R.id.reason_attached_text);
            AtomicReference<String> reasonKeyword = new AtomicReference<>("");
            rGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                int checkedId = radioGroup.getCheckedRadioButtonId();
                attachmentText.setVisibility(View.GONE);
                switch (checkedId) {
                    case R.id.reason_bad_experience: {
                        reasonKeyword.set("REASON_BAD_EXPERIENCE");
                    }
                    case R.id.reason_login_issues: {
                        reasonKeyword.set("REASON_LOGIN_ISSUES");
                    }
                    case R.id.reason_app_useless: {
                        reasonKeyword.set("REASON_APP_USELESS");

                    }
                    case R.id.reason_no_hardware: {
                        reasonKeyword.set("REASON_NO_HARDWARE");

                    }
                    case R.id.reason_other: {
                        reasonKeyword.set("REASON_OTHER");
                        attachmentText.setVisibility(View.VISIBLE);
                    }
                }
            });
            confirmdeletion.setView(R.layout.deletion_dialog_feedback);
            confirmdeletion.setPositiveButton(getString(R.string.confirm_deletion_positive, user.getDisplayName() != null ? user.getDisplayName() : user.getEmail()), (dialogInterface, i) -> {
                loadingdialog = new ProgressDialog(getActivity(), getString(R.string.delete_dialog_loading_text));
                loadingdialog.show();
                JobScheduler scheduler = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                PersistableBundle extra = new PersistableBundle();
                extra.putString("reason", reasonKeyword.toString());
                extra.putString("uid", user.getUid());
                JobInfo info = new JobInfo.Builder(FridayApplication.Jobs.JOB_FEEDBACK, new ComponentName(mActivity, FeedbackService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setBackoffCriteria(30000, JobInfo.BACKOFF_POLICY_LINEAR)
                        .setExtras(extra)
                        .build();
                assert scheduler != null;
                scheduler.schedule(info);
                Task<Void> deletiontask = user.delete();
                deletiontask.addOnCompleteListener(deletioncallback);
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
        if(val){
            devmode_show_changelog.setEnabled(true);
        }
        else{
            devmode_show_changelog.setEnabled(false);
            devmode_show_changelog.setChecked(false);
        }
        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        setHasOptionsMenu(true);
        mActivity = getActivity();
        Preference del_account = findPreference("delete_account");
        Preference sign_out = findPreference("pref_sign_out");
        Preference select_theme_pref = findPreference("dialog_theme_pref");
        Preference auto_check_update = findPreference("check_update_auto");
        Preference auto_sync_account = findPreference("sync_account_auto");
        Preference devmode = findPreference("devmode");
        devmode_show_changelog = (CheckBoxPreference) findPreference("pref_devmode_show_changelog");

        del_account.setOnPreferenceClickListener(deletionlistener);
        sign_out.setOnPreferenceClickListener(sign_out_click);
        select_theme_pref.setOnPreferenceClickListener(select_theme_pref_click);
        auto_check_update.setOnPreferenceChangeListener(autoCheckUpdateChangeListener);
        devmode.setOnPreferenceChangeListener(devModeSwitchChange);
        if (user == null) {
            sign_out.setEnabled(false);
            del_account.setEnabled(false);
            auto_sync_account.setEnabled(false);
        } else {
            del_account.setLayoutResource(R.layout.account_preference_delete);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //This method has to be implented
    }

    private void deleteLocalUserData() {
        File account_file = new File(getContext().getFilesDir(), "/profile/avatar.jpg");
        boolean filedeleted = account_file.delete();
        Log.d("ProfilePage", "Account image file was deleted:" + filedeleted);
    }
}
