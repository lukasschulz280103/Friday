package com.friday.ar.fragments.dialogFragments.changelog

import android.content.Context
import android.text.Spanned
import android.text.SpannedString
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.R
import com.friday.ar.core.util.firebase.FireStoreCodeInterpreter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ChangeLogDialogViewModel(context: Context) : ViewModel() {
    private val updateTitle = MutableLiveData<String>()
    private val updateTimeStamp = MutableLiveData<String>()
    private val updateBody = MutableLiveData<Spanned>()
    private val updateException = MutableLiveData<String>()

    init {
        val pkgInf = context.packageManager.getPackageInfo(context.packageName, 0)
        val changelog = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        changelog.firestoreSettings = settings
        val changelogCollection = changelog.collection("changelogs")
        val changelogDocument = changelogCollection.document(pkgInf.versionName)

        changelogDocument.addSnapshotListener { documentSnapshot, e ->
            if (e == null && documentSnapshot != null && documentSnapshot.exists()) {
                updateTitle.postValue(
                        if (documentSnapshot.contains("title")) documentSnapshot.getString("title")
                        else context.getString(R.string.changelog_dialog_no_title)
                )
                updateTimeStamp.postValue(
                        if (documentSnapshot.contains("release_date")) documentSnapshot.getTimestamp("release_date")!!.toDate().toString()
                        else context.getString(R.string.changelog_dialog_no_timestamp)
                )
                val spannableHtmlBody = if (documentSnapshot.contains("body")) {
                    HtmlCompat.fromHtml(documentSnapshot.getString("body")!!, HtmlCompat.FROM_HTML_MODE_COMPACT)
                } else SpannedString(context.getString(R.string.changelog_dialog_no_body))

                updateBody.postValue(spannableHtmlBody)
            } else if (e != null) {
                val exceptionInterpreter = FireStoreCodeInterpreter(context, e)
                updateException.postValue(exceptionInterpreter.exceptionMessage)
            } else {
                updateException.postValue(ChangelogDialogFragment.NO_UPDATE_INFO_PROVIDED)
            }
        }
    }

    fun getUpdateTitle() = updateTitle as LiveData<String>
    fun getUpdateTimeStamp() = updateTimeStamp as LiveData<String>
    fun getUpdateBody() = updateBody as LiveData<Spanned>
    fun getExceptionMessage() = updateException as LiveData<String>
}