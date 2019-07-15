package com.friday.ar.dashboard

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class DashboardListItem {
    abstract fun getViewHolder(context: Context, parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder)
    abstract fun onRequestDataRefresh(newData: DashboardListItem): Boolean
}