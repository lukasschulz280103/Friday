package com.friday.ar.list.dashboard

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.dashboard.DashboardListItem

class DashboardAdapter(private val context: Context, var dataList: ArrayList<DashboardListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return dataList[viewType].getViewHolder(context, parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        dataList[position].onBindViewHolder(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addItem(item: DashboardListItem, position: Int) {
        dataList.add(position, item)
        notifyItemInserted(position)
    }

    fun onRefresh(newData: ArrayList<DashboardListItem>) {
        val diffUtilResult = DiffUtil.calculateDiff(DashboardDiffUtilCallback(dataList, newData))
        diffUtilResult.dispatchUpdatesTo(this)
        dataList.clear()
        dataList.addAll(newData)
    }
}