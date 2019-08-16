package com.friday.ar.store.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.store.data.PluginInfo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*

class AppCardFragmentViewModel(private val path: String) : ViewModel() {
    private val exception = MutableLiveData<Exception>()
    private val pluginInfo = MutableLiveData<PluginInfo>()
    private val appReference: CollectionReference by lazy {
        FirebaseFirestore.getInstance().collection(path)
    }

    private val completeListener = OnCompleteListener<QuerySnapshot> { task ->
        //loader.visibility = View.GONE
        if (task.isSuccessful) {
            val manifestReference = appReference.document("manifest")
            val metadataReference = appReference.document("metadata")
            val _pluginInfo = PluginInfo()
            manifestReference.addSnapshotListener { snapshot: DocumentSnapshot?, e: Exception? ->
                if (e == null && snapshot != null) {
                    _pluginInfo.title = snapshot.getString("appTitle")
                } else {
                    exception.postValue(e)
                }
            }

            metadataReference.addSnapshotListener { snapshot, e ->
                if (e == null && snapshot != null) {
                    if (snapshot.contains("price")) {
                        _pluginInfo.price = snapshot.getDouble("price")!!
                    } else {
                        exception.postValue(FirebaseFirestoreException("Invalid response recieved from server!", FirebaseFirestoreException.Code.INVALID_ARGUMENT))
                    }
                    pluginInfo.postValue(_pluginInfo)
                } else {
                    exception.postValue(e)
                }
            }
        } else if (task.isCanceled) {
            if (task.exception != null) {
                exception.postValue(task.exception)
            } else {
                exception.postValue(FirebaseFirestoreException("Unknown error!", FirebaseFirestoreException.Code.UNKNOWN))
            }
        }
    }

    init {
        val loadedData = FirebaseFirestore.getInstance().collection(path).get()
        loadedData.addOnCompleteListener(completeListener)
    }

    fun getException() = exception as LiveData<Exception>
    fun getPluginInfo() = pluginInfo as LiveData<PluginInfo>
}