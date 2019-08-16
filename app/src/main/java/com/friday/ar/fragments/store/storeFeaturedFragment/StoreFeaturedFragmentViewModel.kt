package com.friday.ar.fragments.store.storeFeaturedFragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.util.*

class StoreFeaturedFragmentViewModel(val context: Context) : ViewModel() {
    private val fridayStoreFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val dataList = MutableLiveData<ArrayList<CollectionReference>>()
    private val exception = MutableLiveData<FirebaseFirestoreException>()

    private val storeDataLoaded = OnCompleteListener { task: Task<DocumentSnapshot> ->
        if (task.isSuccessful) {
            val _dataList = ArrayList<CollectionReference>()
            val snap = task.result as DocumentSnapshot
            val data = snap.get("list") as ArrayList<Map<String, Any>>?
            for (map in data!!) {
                val collectionReference = fridayStoreFirestore.collection("/store/oldData/" + map["id"])
                _dataList.add(collectionReference)
            }
            dataList.postValue(_dataList)
        } else {
            if (task.exception != null) {
                exception.postValue(task.exception as FirebaseFirestoreException)
            } else {
                exception.postValue(FirebaseFirestoreException("Unknown error occurred!", FirebaseFirestoreException.Code.UNKNOWN))
            }
        }

    }

    init {
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build()
        fridayStoreFirestore.firestoreSettings = firestoreSettings
        val loadedStoreData = fridayStoreFirestore.document("/store/generated/featured-apps/default")
        loadedStoreData.get().addOnCompleteListener(storeDataLoaded)
    }

    fun getDataList() = dataList as LiveData<ArrayList<CollectionReference>>
    fun getException() = exception as LiveData<FirebaseFirestoreException>
}