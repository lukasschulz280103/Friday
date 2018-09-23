package com.code_design_camp.client.friday.HeadDisplayClient.fragments.preferenceFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.SettingsActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class DataSyncPreferenceFragment extends PreferenceFragment {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ProgressDialog loadingdialog;
    View account_pref_view;
    Preference account_pref;
    Preference sign_out;
    Preference del_account;
    Preference.OnPreferenceClickListener sign_out_click = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder confirm_signout = new AlertDialog.Builder(getActivity());
            confirm_signout.setTitle(R.string.confirm_signout_title);
            confirm_signout.setMessage(R.string.confirm_signout_message);
            confirm_signout.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    auth.signOut();
                    deleteLocalUserData();
                    Toast.makeText(getActivity(), getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            });
            confirm_signout.setNegativeButton(android.R.string.no, null);
            confirm_signout.create().show();
            return true;
        }
    };
    OnCompleteListener deletioncallback = new OnCompleteListener() {
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
    Preference.OnPreferenceClickListener deletionlistener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder confirmdeletion = new AlertDialog.Builder(getActivity());
            confirmdeletion.setIcon(R.drawable.ic_warning_black_24dp);
            confirmdeletion.setTitle(R.string.confirm_deletion_title);
            confirmdeletion.setMessage(R.string.confirm_deletion_message);
            confirmdeletion.setView(R.layout.deletion_dialog_feedback);
            confirmdeletion.setPositiveButton(getString(R.string.confirm_deletion_positive, user.getDisplayName()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    loadingdialog = new ProgressDialog(getActivity(), getString(R.string.delete_dialog_loading_text));
                    loadingdialog.show();
                    Task<Void> deletiontask = user.delete();
                    deletiontask.addOnCompleteListener(deletioncallback);
                }
            });
            confirmdeletion.setNegativeButton(android.R.string.cancel, null);
            confirmdeletion.create().show();

            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data_sync);
        setHasOptionsMenu(true);

        account_pref = findPreference("account_main_preference");
        del_account = findPreference("delete_account");
        sign_out = findPreference("pref_sign_out");

        del_account.setOnPreferenceClickListener(deletionlistener);
        sign_out.setOnPreferenceClickListener(sign_out_click);
        if (user == null) {
            sign_out.setEnabled(false);
            del_account.setEnabled(false);
        } else {
            del_account.setLayoutResource(R.layout.account_preference_delete);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteLocalUserData() {
        getActivity().getExternalFilesDir("profile/avatar.jpg").delete();
    }

}
