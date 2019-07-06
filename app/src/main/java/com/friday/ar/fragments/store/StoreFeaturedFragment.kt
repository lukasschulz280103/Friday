package com.friday.ar.fragments.store


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.friday.ar.Constant
import com.friday.ar.fragments.net.ConnectionFragment
import com.friday.ar.store.data.WidgetInfo
import com.friday.ar.store.pager.CardViewPagerAdapter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.util.*


/**
 * Shows the servers featured fragments.
 *
 * @see MainStoreFragment
 */
class StoreFeaturedFragment : Fragment() {
    private var mContext: Context? = null
    private var fridayStoreFirestore: FirebaseFirestore
    private val storeData: DocumentReference? = null
    private val dataList = ArrayList<CollectionReference>()
    private var viewPager: ViewPager? = null

    init {
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build()
        fridayStoreFirestore = FirebaseFirestore.getInstance()
        fridayStoreFirestore.firestoreSettings = firestoreSettings
        // Required empty public constructor
    }

    private val storeDataLoaded = OnCompleteListener<DocumentSnapshot> { task: Task<DocumentSnapshot> ->
        if (task.isSuccessful) {
            val snap = task.result as DocumentSnapshot
            val data = snap.get("list") as ArrayList<Map<String, Any>>?
            for (map in data!!) {
                val inf = WidgetInfo()
                val collectionReference = fridayStoreFirestore.collection("/store/data/" + map["id"])
                dataList.add(collectionReference)
            }
            viewPager!!.adapter = CardViewPagerAdapter(fragmentManager!!, dataList)
        } else {
            val e = task.exception as FirebaseFirestoreException
            Log.d(LOGTAG, e.javaClass.name)
            (parentFragment as ConnectionFragment).onError(e)
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(com.friday.ar.R.layout.fragment_store_featured, container, false)
        viewPager = v.findViewById(com.friday.ar.R.id.store_main_pager)
        val loadedStoreData = fridayStoreFirestore.document("/store/generated/featured-apps/default")
        loadedStoreData.get().addOnCompleteListener(storeDataLoaded)
        return v
    }

    private val LOGTAG = Constant.LOGTAG_STORE

}
