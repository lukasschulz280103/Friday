package com.friday.ar.store.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import androidx.fragment.app.Fragment

import com.friday.ar.Constant
import com.friday.ar.R
import com.friday.ar.dialog.ErrorDialog
import com.friday.ar.fragments.net.ConnectionFragment
import com.friday.ar.store.data.WidgetInfo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot

/**
 * A simple [Fragment] subclass.
 */
class AppCardFragment(private val mAppRef: CollectionReference)//CollectionReference is already apps reference
    : Fragment() {
    internal lateinit var thumbnail: ImageView
    private var errorDialog: ErrorDialog? = null
    private var title: TextView? = null
    private var author: TextView? = null
    private var price: TextView? = null
    private var loader: ProgressBar? = null
    private val completeListener = OnCompleteListener<QuerySnapshot> { task ->
        loader!!.visibility = View.GONE
        if (task.isSuccessful) {
            val manifestReference = mAppRef.document("manifest")
            val metadataReference = mAppRef.document("metadata")
            manifestReference.addSnapshotListener { snapshot, e ->
                if (e == null) {
                    title!!.text = snapshot?.getString("appTitle")
                } else {
                    showErrorDialog(e)
                    (parentFragment!!.parentFragment as ConnectionFragment).onError(e)
                }
            }
            metadataReference.addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val extraInfo = WidgetInfo.ExtraInfo(snapshot?.get("extra") as Map<String, Boolean>)
                    if (snapshot.getDouble("price") == 0.0) {
                        price!!.text = "Free"
                    } else {
                        price!!.text = snapshot.get("price").toString()
                    }
                } else {
                    showErrorDialog(e)
                    (parentFragment!!.parentFragment as ConnectionFragment).onError(e)
                }
            }
        } else if (task.isCanceled) {
            val e = task.exception
            showErrorDialog(e)
            (parentFragment!!.parentFragment as ConnectionFragment).onError(e!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.store_app_card, container, false)
        thumbnail = v.findViewById(R.id.thumb)
        title = v.findViewById(R.id.card_title)
        author = v.findViewById(R.id.authorName)
        price = v.findViewById(R.id.price)
        loader = v.findViewById(R.id.loader)
        val loadedData = mAppRef.get()
        loadedData.addOnCompleteListener(completeListener)
        return v
    }

    private fun showErrorDialog(e: Exception?) {
        if (errorDialog == null || !errorDialog!!.isShown) {
            errorDialog = ErrorDialog(activity!!, R.drawable.ic_warning_black_24dp, e!!)
            errorDialog!!.show()
        }
    }

    companion object {
        private val LOGTAG = Constant.LOGTAG_STORE
    }
}
