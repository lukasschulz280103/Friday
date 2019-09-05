package com.friday.ar.dashboard.list

import androidx.recyclerview.widget.DiffUtil
import com.friday.ar.dashboard.internal.base.BaseDashboardListItem

class DashboardDiffUtilCallback(private val oldData: ArrayList<BaseDashboardListItem>, private val newData: ArrayList<BaseDashboardListItem>) : DiffUtil.Callback() {
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