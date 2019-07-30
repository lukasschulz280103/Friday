package com.friday.ar.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import com.friday.ar.R
import com.friday.ar.Theme
import com.friday.ar.activities.FridayActivity
import com.friday.ar.dialog.ProgressDialog
import com.friday.ar.util.Connectivity
import com.friday.ar.util.LogUtil
import com.friday.ar.util.Validator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_feedback.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class FeedbackSenderActivity : FridayActivity() {
    companion object {
        private const val LOGTAG = "FeedbackSenderActivity"
    }

    private var firebaseStorage = FirebaseStorage.getInstance()
    private var feedbackLogFolder = firebaseStorage.getReference("feedback")

    private lateinit var inputtemplate: SharedPreferences
    private lateinit var fileUploadDialog: ProgressDialog

    private lateinit var attachedImageBitmpap: Bitmap
    private var showInfo = View.OnLongClickListener { view ->
        val id = view.id
        val infoDialog = MaterialAlertDialogBuilder(this@FeedbackSenderActivity)
        infoDialog.setPositiveButton(android.R.string.ok, null)
        when (id) {
            R.id.feedback_usage_data -> infoDialog.setView(R.layout.infodialog_debug_usage_data)
            R.id.feedback_device_info -> infoDialog.setView(R.layout.infodialog_debug_device)
            else -> Log.w(LOGTAG, "view id didn't match cases")
        }
        infoDialog.create().show()
        false
    }
    private var attachedFile: File? = null

    private val attachmentUpload = OnCompleteListener<UploadTask.TaskSnapshot> { task ->
        fileUploadDialog.dismiss()
        if (task.isSuccessful) {
            Toast.makeText(this@FeedbackSenderActivity, R.string.feedback_submit_success, Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this@FeedbackSenderActivity, R.string.smth_went_wrong, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Theme.getCurrentAppTheme(this))
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_feedback)
        val user = FirebaseAuth.getInstance().currentUser
        setSupportActionBar(toolbar2)
        inputtemplate = PreferenceManager.getDefaultSharedPreferences(this)
        if (user != null) {
            feedback_mail.setText(user.email)
        }
        feedback_usage_data.setOnLongClickListener(showInfo)
        feedback_device_info.setOnLongClickListener(showInfo)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_send_feedback, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.feedback_attach_file -> {
                val reqAttachFile = Intent()
                reqAttachFile.type = "image/*"
                reqAttachFile.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(reqAttachFile, 1)
                return true
            }
            R.id.feedback_submit -> {
                if (Connectivity.isConnected(this)) {
                    if (Validator.validateEmail(feedback_mail.text.toString())) {
                        feedback_mail.error = null
                        return submitFeedback()
                    } else {
                        feedback_mail.error = getString(R.string.mail_invalid_error)
                    }
                } else {
                    val errorui = MaterialAlertDialogBuilder(this)
                    errorui.setTitle(R.string.network_error_title)
                    errorui.setMessage(R.string.error_network_missing_info)
                    errorui.setPositiveButton(R.string.retry) { _, _ -> onOptionsItemSelected(item) }
                    errorui.create().show()
                }
            }
        }
        return false
    }

    private fun createTimeStampString(): String {
        val timestampcalendar = GregorianCalendar.getInstance()
        val timestampbuilder = StringBuilder()
                .append(timestampcalendar.get(Calendar.YEAR))
                .append(timestampcalendar.get(Calendar.DAY_OF_MONTH))
                .append(timestampcalendar.get(Calendar.MONTH).toString() + "_")
                .append(timestampcalendar.get(Calendar.HOUR_OF_DAY))
                .append(timestampcalendar.get(Calendar.MINUTE))
                .append(timestampcalendar.get(Calendar.SECOND))
        return timestampbuilder.toString()
    }

    private fun submitFeedback(): Boolean {
        if (feedback_device_info.isChecked || feedback_body.text!!.toString().trim { it <= ' ' }.isNotEmpty()) {
            try {
                val deviceInfoFile = LogUtil.createDebugInfoFile(this, "email", feedback_mail.text!!.toString(), "body", feedback_body.text!!.toString())
                val folderName = createTimeStampString()
                var uploadDeviceInfoFile: UploadTask? = null
                fileUploadDialog = ProgressDialog(this, getString(R.string.feedback_submit_logfiles_upload))
                fileUploadDialog.show()
                uploadDeviceInfoFile = feedbackLogFolder.child("$folderName/device_info.json").putBytes(LogUtil.fileToString(deviceInfoFile!!).toByteArray())
                uploadDeviceInfoFile.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (attachedFile != null) {
                            val byteStream = ByteArrayOutputStream()
                            attachedImageBitmpap.compress(Bitmap.CompressFormat.PNG, 50, byteStream)
                            attachedImageBitmpap.recycle()
                            fileUploadDialog.setMessage(R.string.feedback_submit_image_upload)
                            val uploadImageFile = feedbackLogFolder.child("$folderName/feedback-image.jpg").putBytes(byteStream.toByteArray())
                            uploadImageFile.addOnCompleteListener(attachmentUpload)
                        } else {
                            Toast.makeText(this@FeedbackSenderActivity, R.string.feedback_submit_success, Toast.LENGTH_LONG).show()
                            Log.d(LOGTAG, "Submitted log without attached file")
                            finish()
                        }
                    } else {
                        fileUploadDialog.dismiss()
                        val e = task.exception
                        Log.e(LOGTAG, e!!.localizedMessage, e)
                        val errorDialog = MaterialAlertDialogBuilder(this)
                        errorDialog.setTitle(R.string.simple_action_error_title)

                        if (e is StorageException) {
                            errorDialog.setMessage(R.string.feedbackSender_storageException)
                        } else errorDialog.setMessage(e.localizedMessage)

                        errorDialog.setPositiveButton(android.R.string.ok, null)
                                .create().show()
                    }
                }
            } catch (e: FileNotFoundException) {
                Log.e(LOGTAG, e.localizedMessage, e)
            }

        } else {
            val missinginputserror = MaterialAlertDialogBuilder(this)
            missinginputserror.setMessage(R.string.missing_inputs)
            missinginputserror.setPositiveButton(android.R.string.ok, null)
            missinginputserror.create().show()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            return
        }
        if (requestCode == 1) {
            Log.d(LOGTAG, "resultcode is $resultCode")
            if (attachedFile == null) {
                try {
                    Log.d(LOGTAG, "Uri oldData in OnActivity return is " + data!!.data!!)
                    val mUri = data.data
                    attachedFile = File(data.data!!.path!!)
                    attachedImageBitmpap = MediaStore.Images.Media.getBitmap(contentResolver, mUri)
                    Toast.makeText(this, getString(R.string.new_file_attached, attachedFile!!.name), Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(this, getString(R.string.changed_attached_file, attachedFile!!.name), Toast.LENGTH_LONG).show()
            }
            Log.d("FeedbackAttachedFile", attachedFile!!.path)
        }
    }
}
