package com.friday.ar.fragments.store


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

import com.friday.ar.R
import com.friday.ar.fragments.net.ConnectionFragment
import com.friday.ar.fragments.net.OnConnectionStateChangedListener
import com.google.firebase.firestore.FirebaseFirestoreException


class MainStoreFragment : ConnectionFragment(), OnConnectionStateChangedListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("storefragment", "creating store view")
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_main_store, container, false)
        val toolbar = v.findViewById<Toolbar>(R.id.store_toolbar)
        toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        activity!!.invalidateOptionsMenu()
        //addFragment(R.id.store_featured_fragment_container, StoreFeaturedFragment())
        return v
    }

    override fun onConnected() {
        if (childFragmentManager.backStackEntryCount != 0) childFragmentManager.popBackStack()
    }

    override fun onError(e: Exception) {
        Log.e("StoreError", "Error occured at store")
        childFragmentManager.beginTransaction()
                .add(R.id.store_frag_container, ErrorFragment(e as FirebaseFirestoreException))
                .remove(this)
                .commitAllowingStateLoss()
    }

    private fun addFragment(@IdRes containerId: Int, newFragment: Fragment) {
        childFragmentManager.beginTransaction()
                .replace(containerId, newFragment)
                .commitAllowingStateLoss()
    }
}
