package com.friday.ar.dashboard.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.friday.ar.dashboard.R


class DashboardSimpleItem : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dashboard_item_simple, container, false)
        val title = v.findViewById<TextView>(R.id.title)
        val toStore = v.findViewById<Button>(R.id.morebtn)
        return v
    }
}
