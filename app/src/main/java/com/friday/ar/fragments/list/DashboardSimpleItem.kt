package com.friday.ar.fragments.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.friday.ar.R
import com.friday.ar.ui.store.StoreDetailActivity

class DashboardSimpleItem : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dashboard_item_simple, container, false)
        val title = v.findViewById<TextView>(R.id.title)
        val toStore = v.findViewById<Button>(R.id.morebtn)
        title.setText(R.string.recommendation_picked_widgets_title)
        toStore.setText(R.string.to_store)
        toStore.setOnClickListener { startActivity(Intent(activity, StoreDetailActivity::class.java)) }
        return v
    }
}
