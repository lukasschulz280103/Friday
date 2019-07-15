package com.friday.ar.list.dashboard

import androidx.recyclerview.widget.DiffUtil
import com.friday.ar.dashboard.DashboardListItem

class DashboardDiffUtilCallback(private val oldData: ArrayList<DashboardListItem>, private val newData: ArrayList<DashboardListItem>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].onRequestDataRefresh(newData[newItemPosition])
    }
}