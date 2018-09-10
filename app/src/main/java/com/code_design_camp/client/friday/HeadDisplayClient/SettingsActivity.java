package com.code_design_camp.client.friday.HeadDisplayClient;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View account_pref_view;
        Preference account_pref;
        Preference sign_out;
        Preference del_account;
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
                        Toast.makeText(getActivity(), getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show();
                    }
                });
                confirm_signout.setNegativeButton(android.R.string.no,null);
                confirm_signout.create().show();
                return true;
            }
        };
        Preference.OnPreferenceClickListener deletionlistener  = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder confirmdeletion = new AlertDialog.Builder(getActivity());
                confirmdeletion.setIcon(R.drawable.ic_warning_black_24dp);
                confirmdeletion.setTitle(R.string.confirm_deletion_title);
                confirmdeletion.setMessage(R.string.confirm_deletion_message);
                confirmdeletion.setPositiveButton(getString(R.string.confirm_deletion_positive, user.getDisplayName()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Task<Void> deletiontask = user.delete();
                        deletiontask.addOnCompleteListener(deletioncallback);
                    }
                });
                confirmdeletion.setNegativeButton(android.R.string.cancel,null);
                confirmdeletion.create().show();
                return false;
            }
        };
        OnCompleteListener deletioncallback = new OnCompleteListener() {
            AlertDialog.Builder faildeletedialog;
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Deleted account",Toast.LENGTH_LONG).show();
                }
                else if(task.isCanceled()){
                    faildeletedialog.setTitle("Deletion failed.");
                    faildeletedialog.setMessage("Deletion process was canceled");
                    faildeletedialog.setPositiveButton(android.R.string.ok,null);
                    faildeletedialog.create().show();
                }
                else{
                    //Failurecode soon.
                }
            }
        };
    }
}
