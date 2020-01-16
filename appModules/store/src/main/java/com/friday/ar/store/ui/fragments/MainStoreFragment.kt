package com.friday.ar.store.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.friday.ar.core.net.ConnectionFragment
import com.friday.ar.core.net.OnConnectionStateChangedListener
import com.friday.ar.store.R
import com.friday.ar.store.ui.fragments.feed.gridFragments.SimpleGridFragment
import extensioneer.notNull
import org.koin.android.viewmodel.ext.android.viewModel


class MainStoreFragment : ConnectionFragment(), OnConnectionStateChangedListener {
    companion object {
        private const val LOGTAG = "MainStoreFragment"
    }

    val storeViewModel by viewModel<StoreViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("storefragment", "creating store view")
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_main_store, container, false)
        val toolbar = v.findViewById<Toolbar>(R.id.store_toolbar)
        toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        activity!!.invalidateOptionsMenu()

        storeViewModel.structureData().observe(this, Observer { itemList ->
            itemList.forEach { document ->
                Log.d(LOGTAG, "found feed item: [id:${document.id}; type=${document.getString("type")}; title=${document.getString("title")}]")
                val simpleGridFragment = SimpleGridFragment.from(document)
                simpleGridFragment.notNull {
                    this@MainStoreFragment.childFragmentManager.beginTransaction()
                            .add(R.id.contentStore, this)
                            .addToBackStack(null)
                            .commit()
                }
            }
        })
        return v
    }

    override fun onConnected() {
        //if (childFragmentManager.backStackEntryCount != 0) childFragmentManager.popBackStack()
    }

    override fun onError(e: Exception) {
        Log.e("StoreError", "Error occured at store: ${e.message}")
    }
}
