package com.friday.ar.fragments.dialogFragments


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.friday.ar.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.util.*

class ChangelogDialogFragment : DialogFragment() {
    private var container: LinearLayout? = null
    private var title: TextView? = null
    private var timestamp: TextView? = null
    private var body: TextView? = null
    private var errortext: TextView? = null
    private var loadingbar: ProgressBar? = null
    private var dismiss: MaterialButton? = null

    private var updateTitle: String? = null
        set(value) {
            field = value
            title!!.text = value
        }
    private var updateBody: String? = null
        set(value) {
            field = value
            body!!.text = value
        }
    private var updateTime: Date? = null
        set(value) {
            field = value
            timestamp!!.text = value.toString()
        }
    private val changelog = FirebaseFirestore.getInstance()
    private var changelogCollection: CollectionReference? = null
    private val onChangelogDocumentLoaded = EventListener<DocumentSnapshot> { documentSnapshot, e ->
        if (e == null && documentSnapshot != null && documentSnapshot.exists()) {
            updateTitle = documentSnapshot.get("title") as String?
            updateBody = documentSnapshot.get("body") as String?
            updateTime = (documentSnapshot.get("release_date") as Timestamp).toDate()
            Log.d(LOGTAG, java.lang.Boolean.valueOf(documentSnapshot.get("release_date") == null).toString())
            container!!.visibility = View.VISIBLE
            loadingbar!!.visibility = View.GONE
            body!!.text = HtmlCompat.fromHtml(updateBody!!, HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else if (e != null) {
            errortext!!.visibility = View.VISIBLE
            errortext!!.text = String.format("Couldn't load info. Caused by: %s", e.localizedMessage)
            isCancelable = true
            loadingbar!!.visibility = View.GONE
            Log.e(LOGTAG, e.localizedMessage, e)
        } else {
            dismiss()
            Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
        }
        dismiss!!.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isCancelable = false
        try {
            super.onCreate(savedInstanceState)
            val pkgInf = activity!!.packageManager.getPackageInfo(activity!!.packageName, 0)
            Log.d("ChangeLogDialog", "versionName is " + pkgInf.versionName)
            val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
            changelog.firestoreSettings = settings
            changelogCollection = changelog.collection("changelogs")
            val changelogDocument = changelogCollection!!.document(pkgInf.versionName)
            changelogDocument.addSnapshotListener(onChangelogDocumentLoaded)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val dialogView = inflater.inflate(R.layout.fragment_changelog_dialog, container, false)
        title = dialogView.findViewById(R.id.changelog_dialog_title)
        timestamp = dialogView.findViewById(R.id.changelog_dialog_release_date)
        body = dialogView.findViewById(R.id.changelog_dialog_body)
        errortext = dialogView.findViewById(R.id.chld_error)
        loadingbar = dialogView.findViewById(R.id.changelog_dialog_progress)
        this.container = dialogView.findViewById(R.id.chld_infocontainer)
        dismiss = dialogView.findViewById(R.id.chld_dismissbtn)
        dismiss!!.setOnClickListener { dismiss() }
        dismiss!!.isEnabled = false
        return dialogView
    }

    companion object {
        private const val LOGTAG = "ChangeLogDialog"
    }

}
