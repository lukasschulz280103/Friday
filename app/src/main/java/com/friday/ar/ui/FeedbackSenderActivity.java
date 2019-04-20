package com.friday.ar.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.activities.FridayActivity;
import com.friday.ar.dialog.ProgressDialog;
import com.friday.ar.util.Connectivity;
import com.friday.ar.util.LogUtil;
import com.friday.ar.util.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FeedbackSenderActivity extends FridayActivity {
    private static final String LOGTAG = "FeedbackSenderActivity";
    AppCompatEditText feedback_email;
    AppCompatEditText feedback_body;
    AppCompatCheckBox feedback_debug_add_device_info;
    AppCompatCheckBox feedback_debug_add_usage_data;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference feedback_log_folder = firebaseStorage.getReference("feedback");

    SharedPreferences inputtemplate;
    ProgressDialog fileUploadDialog;

    Bitmap attachedImageBitmpap;
    View.OnLongClickListener showInfo = view -> {
        int id = view.getId();
        MaterialAlertDialogBuilder infoDialog = new MaterialAlertDialogBuilder(FeedbackSenderActivity.this);
        infoDialog.setPositiveButton(android.R.string.ok, null);
        switch (id) {
            case R.id.feedback_usage_data:
                infoDialog.setView(R.layout.infodialog_debug_usage_data);
                break;
            case R.id.feedback_device_info:
                infoDialog.setView(R.layout.infodialog_debug_device);
                break;
            default:
                Log.w(LOGTAG, "view id didn't match cases");
                break;
        }
        infoDialog.create().show();
        return false;
    };
    private File attachedFile;
    private OnCompleteListener<UploadTask.TaskSnapshot> attachment_upload = new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(Task<UploadTask.TaskSnapshot> task) {
            fileUploadDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(FeedbackSenderActivity.this, R.string.feedback_submit_success, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(FeedbackSenderActivity.this, R.string.smth_went_wrong, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);
        feedback_email = findViewById(R.id.feedback_mail);
        feedback_body = findViewById(R.id.feedback_body);
        feedback_debug_add_device_info = findViewById(R.id.feedback_device_info);
        feedback_debug_add_usage_data = findViewById(R.id.feedback_usage_data);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Toolbar t = findViewById(R.id.toolbar2);
        setSupportActionBar(t);
        inputtemplate = PreferenceManager.getDefaultSharedPreferences(this);
        if (user != null) {
            feedback_email.setText(user.getEmail());
        }
        feedback_debug_add_usage_data.setOnLongClickListener(showInfo);
        feedback_debug_add_device_info.setOnLongClickListener(showInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuItem mItem = item;
        switch (item.getItemId()) {
            case R.id.feedback_attach_file: {
                Intent reqAttachFile = new Intent();
                reqAttachFile.setType("image/*");
                reqAttachFile.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(reqAttachFile, 1);
                return true;
            }
            case R.id.feedback_submit: {
                if (Connectivity.isConnected(this)) {
                    if (Validator.validateEmailInput(FeedbackSenderActivity.this, feedback_email)) {
                        return submitFeedback();
                    }
                } else {
                    MaterialAlertDialogBuilder errorui = new MaterialAlertDialogBuilder(this);
                    errorui.setTitle(R.string.network_error_title);
                    errorui.setMessage(R.string.error_network_missing_info);
                    errorui.setPositiveButton(R.string.retry, (dialogInterface, i) -> onOptionsItemSelected(mItem));
                    errorui.create().show();
                }
            }
        }
        return false;
    }

    private String createTimeStampString() {
        Calendar timestampcalendar = GregorianCalendar.getInstance();
        StringBuilder timestampbuilder = new StringBuilder()
                .append(timestampcalendar.get(Calendar.YEAR))
                .append(timestampcalendar.get(Calendar.DAY_OF_MONTH))
                .append(timestampcalendar.get(Calendar.MONTH) + "_")
                .append(timestampcalendar.get(Calendar.HOUR_OF_DAY))
                .append(timestampcalendar.get(Calendar.MINUTE))
                .append(timestampcalendar.get(Calendar.SECOND));
        return timestampbuilder.toString();
    }

    private boolean submitFeedback() {
        if (feedback_debug_add_device_info.isChecked() || !feedback_body.getText().toString().trim().isEmpty()) {
            try {
                File device_info_file = LogUtil.createDebugInfoFile(this, "email", feedback_email.getText().toString(), "body", feedback_body.getText().toString());
                final String folder_name = createTimeStampString();
                UploadTask upload_device_info_file = null;
                fileUploadDialog = new ProgressDialog(this, getString(R.string.feedback_submit_logfiles_upload));
                fileUploadDialog.show();
                upload_device_info_file = feedback_log_folder.child(folder_name + "/device_info.json").putBytes(LogUtil.fileToString(device_info_file).getBytes());
                upload_device_info_file.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (attachedFile != null) {
                            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
                            attachedImageBitmpap.compress(Bitmap.CompressFormat.PNG, 50, bytestream);
                            attachedImageBitmpap.recycle();
                            fileUploadDialog.setMessage(R.string.feedback_submit_image_upload);
                            UploadTask upload_image_file = feedback_log_folder.child(folder_name + "/feedback-image.jpg").putBytes(bytestream.toByteArray());
                            upload_image_file.addOnCompleteListener(attachment_upload);
                        } else {
                            Toast.makeText(FeedbackSenderActivity.this, R.string.feedback_submit_success, Toast.LENGTH_LONG).show();
                            Log.d(LOGTAG, "Submitted log without attached file");
                            finish();
                        }
                    } else {
                        Exception e = task.getException();
                        Log.e(LOGTAG, e.getLocalizedMessage(), e);
                    }
                });
            } catch (FileNotFoundException e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
            }
        } else {
            MaterialAlertDialogBuilder missinginputserror = new MaterialAlertDialogBuilder(this);
            missinginputserror.setMessage(R.string.missing_inputs);
            missinginputserror.setPositiveButton(android.R.string.ok, null);
            missinginputserror.create().show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 0) {
            return;
        }
        if (requestCode == 1) {
            Log.d(LOGTAG, "resultcode is " + resultCode);
            if (attachedFile == null) {
                try {
                    Log.d(LOGTAG, "Uri data in OnActivity return is " + data.getData());
                    Uri mUri = data.getData();
                    attachedFile = new File(data.getData().getPath());
                    attachedImageBitmpap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
                    Toast.makeText(this, getString(R.string.new_file_attached, attachedFile.getName()), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, getString(R.string.changed_attached_file, attachedFile.getName()), Toast.LENGTH_LONG).show();
            }
            Log.d("FeedbackAttachedFile", attachedFile.getPath());
        }
    }
}
