package com.friday.ar.store.ui.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.core.Constant
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import extensioneer.isNull
import extensioneer.notNull

class StoreViewModel : ViewModel() {

    private val structureData = MutableLiveData<List<DocumentSnapshot>>()
    private val errorData = MutableLiveData<Exception>()

    init {
        val storeDatabase = FirebaseFirestore.getInstance()
        val feedStructureData = storeDatabase.collection(Constant.Server.Database.STORE_FEED_BASE_STRUCTURE_REFERENCE)

        feedStructureData.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                snapshot.notNull {
                    structureData.postValue(documents)
                }.isNull {
                    errorData.postValue(task.exception)
                }
            } else {
                Log.e("ERROR", "GET FAILED: message: ${task.exception!!.message}")
            }
        }
    }

    fun structureData() = structureData as LiveData<List<DocumentSnapshot>>
}