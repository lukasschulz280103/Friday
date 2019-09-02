package com.friday.ar.store.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import com.friday.ar.core.activity.FridayActivity
import com.friday.ar.store.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_store_detail.*
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

//TODO rewrite this class
class StoreDetailActivity : FridayActivity() {
    companion object {
        private const val LOGTAG = "StoreDetailActivity"
        private const val ONE_MB = (1024 * 1024).toLong()
    }

    private var manifestListener = { task: Task<*> ->
        if (task.isCanceled) {

        } else {

        }
    }
    private val appStorage = FirebaseStorage.getInstance()
    private val appStorageRef = appStorage.getReference("store/widgets/001")
    private val appMetaData = FirebaseFirestore.getInstance()
    private val reviewRef = appMetaData.collection("store/review/001")
    private val reviewRefTask = reviewRef.get()
    private val metaDoc = reviewRef.document("meta").get()
    private var manifestDownloadTask: Task<*>? = null
    private val appLogoTask: Task<*>? = null
    private var manifest: JSONObject? = null
    private val manifestSuccessListener = OnSuccessListener<Any> { o ->
        try {
            Log.d(LOGTAG, "Loaded oldData")
            manifest = JSONObject(String(o as ByteArray, Charset.forName("UTF-8")))
            val tags = manifest!!.getJSONArray("tags")
            widget_detail_loading_spinner!!.visibility = View.GONE
            enableScroll()
            start_download!!.isEnabled = true
            store_collapsing_bar!!.title = manifest!!.getString("name")
            store_widget_version_header!!.text = getString(R.string.store_detail_version, manifest!!.get("versionName"))
            if (manifest!!.getString("versionExtra") == "beta") {
                start_download!!.setText(R.string.beta)
                start_download!!.setStrokeColorResource(R.color.colorBeta)
                start_download!!.setTextColor(getColor(R.color.colorBeta))
            }
            for (i in 0 until tags.length()) {
                val tag = Chip(this@StoreDetailActivity)
                tag.text = tags.get(i) as String
                store_detail_tags_group!!.addView(tag)
            }
        } catch (e: JSONException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        }
    }
    private val logoListener = OnSuccessListener<QuerySnapshot> {
    }
    private val logoSuccesListener = OnCompleteListener<Any> { task ->
        if (task.isCanceled) {

        } else {

        }
    }
    private val reviewListener = OnCompleteListener<QuerySnapshot> { task ->
        if (task.isCanceled) {

        } else {

        }
    }
    private val reviewSuccessListener = OnSuccessListener<QuerySnapshot> { o ->
        val snapshot = o as QuerySnapshot
    }
    private val metaDataListener = OnCompleteListener<DocumentSnapshot> {
    }
    private val metaDataSuccessListener = OnSuccessListener<DocumentSnapshot> { o ->
        val doc = o as DocumentSnapshot
        widget_rating_server!!.rating = (doc.get("rating_stars") as Double).toInt().toFloat()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_store_detail)
        setSupportActionBar(store_detail_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        disableScroll()
        store_collapsing_bar!!.title = ""
        start_download!!.isEnabled = false
        manifestDownloadTask = appStorageRef.child("/manifest.json").getBytes(ONE_MB)
        reviewRefTask.addOnSuccessListener(reviewSuccessListener)
        reviewRefTask.addOnCompleteListener(reviewListener)
        //appLogoTask = appStorageRef.child("oldData/logo/logo.png").getBytes(FIVE_MB);
        addStorageListeners()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.store_detail_app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                run { finish() }
                run { return false }
            }
            else -> {
                return false
            }
        }
    }

    private fun addStorageListeners() {
        manifestDownloadTask!!.addOnCompleteListener(manifestListener)
        manifestDownloadTask!!.addOnSuccessListener(manifestSuccessListener)
        metaDoc.addOnCompleteListener(metaDataListener)
        metaDoc.addOnSuccessListener(metaDataSuccessListener)
        //appLogoTask.addOnSuccessListener(logoListener);
        //appLogoTask.addOnCompleteListener(logoSuccessListener);
    }

    private fun enableScroll() {
        val params = store_collapsing_bar!!.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        store_collapsing_bar!!.layoutParams = params
    }

    private fun disableScroll() {
        val params = store_collapsing_bar!!.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        store_collapsing_bar!!.layoutParams = params
    }
}
