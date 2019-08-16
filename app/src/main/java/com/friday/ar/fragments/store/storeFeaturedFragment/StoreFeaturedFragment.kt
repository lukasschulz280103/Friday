package com.friday.ar.fragments.store.storeFeaturedFragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.friday.ar.fragments.net.ConnectionFragment
import com.friday.ar.fragments.store.MainStoreFragment
import com.friday.ar.store.pager.CardViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_store_featured.*


/**
 * Shows the servers featured fragments.
 *
 * @see MainStoreFragment
 */
class StoreFeaturedFragment : ConnectionFragment() {
    companion object {
        private const val LOGTAG = "StoreMainPager"
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(StoreFeaturedFragmentViewModel::class.java)
    }

    private lateinit var mContext: Context




    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.friday.ar.R.layout.fragment_store_featured, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDataList().observe(this, Observer { data ->
            store_main_pager.adapter = CardViewPagerAdapter(fragmentManager!!, data)
        })
        viewModel.getException().observe(this, Observer { exception ->
            (parentFragment as MainStoreFragment).onError(exception)
        })
    }
}
