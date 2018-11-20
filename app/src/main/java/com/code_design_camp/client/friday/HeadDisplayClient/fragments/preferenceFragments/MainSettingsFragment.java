package com.code_design_camp.client.friday.HeadDisplayClient.fragments.preferenceFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ProgressDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ThemeDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.preference.ThemeSelectPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class MainSettingsFragment extends PreferenceFragmentCompat {
    private static final String LOGTAG = "SettingsFragment";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog loadingdialog;
    private Preference account_pref, sign_out, del_account, select_theme_pref;
    private Intent themeResultIntent;

    private ThemeDialog.OnSelectedTheme themeSelected = (t, r) -> {
        Log.d("SetttingsActivity", "onThemeSelected");
    };
    private Preference.OnPreferenceClickListener select_theme_pref_click = preference -> {
        ThemeSelectPreference pref = (ThemeSelectPreference) preference;
        pref.showDialog(themeSelected);
        return true;
    };
    private Preference.OnPreferenceClickListener sign_out_click = preference -> {
        AlertDialog.Builder confirm_signout = new AlertDialog.Builder(getActivity());
        confirm_signout.setTitle(R.string.confirm_signout_title);
        confirm_signout.setMessage(R.string.confirm_signout_message);
        confirm_signout.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            auth.signOut();
            deleteLocalUserData();
            Toast.makeText(getActivity(), getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });
        confirm_signout.setNegativeButton(android.R.string.no, null);
        confirm_signout.create().show();
        return true;
    };
    private OnCompleteListener deletioncallback = new OnCompleteListener() {
        AlertDialog.Builder faildeletedialog;

        @Override
        public void onComplete(@NonNull Task task) {
            faildeletedialog = new AlertDialog.Builder(getActivity());
            loadingdialog.dismiss();
            if (task.isSuccessful()) {
                deleteLocalUserData();
                Toast.makeText(getActivity(), getString(R.string.deletion_success), Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else if (task.isCanceled()) {
                faildeletedialog.setTitle(R.string.deletion_error_canceled_title);
                faildeletedialog.setMessage(R.string.deletion_error_canceled_message);
                faildeletedialog.setPositiveButton(android.R.string.ok, null);
                faildeletedialog.create().show();
            } else {
                faildeletedialog.setTitle(R.string.deletion_error_unknown_title);
                faildeletedialog.setMessage(task.getException().getMessage());
                faildeletedialog.setPositiveButton(android.R.string.ok, null);
                faildeletedialog.create().show();
                Log.e("AccountDeletion", "Could not delete account", task.getException());
            }
        }
    };
    private Preference.OnPreferenceClickListener deletionlistener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder confirmdeletion = new AlertDialog.Builder(getActivity());
            confirmdeletion.setIcon(R.drawable.ic_warning_black_24dp);
            confirmdeletion.setTitle(R.string.confirm_deletion_title);
            confirmdeletion.setMessage(R.string.confirm_deletion_message);
            confirmdeletion.setView(R.layout.deletion_dialog_feedback);
            confirmdeletion.setPositiveButton(getString(R.string.confirm_deletion_positive, user.getDisplayName()), (dialogInterface, i) -> {
                loadingdialog = new ProgressDialog(getActivity(), getString(R.string.delete_dialog_loading_text));
                loadingdialog.show();
                Task<Void> deletiontask = user.delete();
                deletiontask.addOnCompleteListener(deletioncallback);
            });
            confirmdeletion.setNegativeButton(android.R.string.cancel, null);
            confirmdeletion.create().show();

            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        setHasOptionsMenu(true);


        account_pref = findPreference("account_main_preference");
        del_account = findPreference("delete_account");
        sign_out = findPreference("pref_sign_out");
        select_theme_pref = findPreference("dialog_theme_pref");

        del_account.setOnPreferenceClickListener(deletionlistener);
        sign_out.setOnPreferenceClickListener(sign_out_click);
        select_theme_pref.setOnPreferenceClickListener(select_theme_pref_click);
        if (user == null) {
            sign_out.setEnabled(false);
            del_account.setEnabled(false);
        } else {
            del_account.setLayoutResource(R.layout.account_preference_delete);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    private void deleteLocalUserData() {
        File account_file = new File(getContext().getFilesDir(), "/profile/avatar.jpg");
        boolean filedeleted = account_file.delete();
        Log.d("ProfilePage", "Account image file was deleted:" + filedeleted);
    }

    public Intent getThemeResultIntent() {
        return this.themeResultIntent;
    }
}
