package com.friday.ar.store.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.friday.ar.R
import kotlinx.android.synthetic.main.store_app_card.*

/**
 * A simple [Fragment] subclass.
 */
class AppCardFragment : Fragment() {
    companion object {
        private const val LOGTAG = "AppCardFragment"
    }

    private val viewModel: AppCardFragmentViewModel by lazy {
        ViewModelProviders.of(this).get(AppCardFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.store_app_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getException().observe(this, Observer { exception ->
            if (exception.message != null) Log.e(LOGTAG, exception.localizedMessage!!)
        })
        viewModel.getPluginInfo().observe(this, Observer { pluginInfo ->
            card_title.text = pluginInfo.title
            price.text = pluginInfo.price.toString()
            authorName.text = pluginInfo.authorName
        })
    }
}
