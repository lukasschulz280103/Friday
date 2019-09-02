package com.friday.ar.store.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.friday.ar.store.R

//TODO: create viewModel
class AppCardFragment : Fragment() {
    companion object {
        private const val LOGTAG = "AppCardFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.store_app_card, container, false)
    }

}
