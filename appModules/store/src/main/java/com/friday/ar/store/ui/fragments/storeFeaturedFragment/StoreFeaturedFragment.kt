package com.friday.ar.store.ui.fragments.storeFeaturedFragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.friday.ar.core.net.ConnectionFragment
import com.friday.ar.store.R
import com.friday.ar.store.pager.CardViewPagerAdapter
import com.friday.ar.store.ui.fragments.MainStoreFragment
import kotlinx.android.synthetic.main.fragment_store_featured.*
import org.koin.android.viewmodel.ext.android.viewModel


/**
 * Shows the servers featured fragments.
 *
 * @see MainStoreFragment
 */
class StoreFeaturedFragment : ConnectionFragment() {
    companion object {
        private const val LOGTAG = "StoreMainPager"
    }

    private val viewModel by viewModel<StoreFeaturedFragmentViewModel>()

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store_featured, container, false)
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
