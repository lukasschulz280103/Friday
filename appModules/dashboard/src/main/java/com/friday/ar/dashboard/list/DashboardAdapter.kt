package com.friday.ar.dashboard.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.dashboard.R
import com.friday.ar.dashboard.internal.base.BaseDashboardListItem
import java.util.*

class DashboardAdapter(private val context: Context, private var dataList: ArrayList<BaseDashboardListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BaseDashboardListItem.BaseDashboardItemViewHolder(LayoutInflater.from(context).inflate(R.layout.dashboard_base_item, parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as BaseDashboardListItem.BaseDashboardItemViewHolder
        viewHolder.itemIcon.setImageDrawable(context.getDrawable(R.drawable.twotone_notifications_24))
        dataList[position].plugin.observeForever { plugin ->
            viewHolder.itemPluginName.text = plugin.name
        }
        viewHolder.itemSmallText.text = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addItem(item: BaseDashboardListItem, position: Int) {
        dataList.add(position, item)
        notifyItemInserted(position)
    }

    fun onRefresh(newData: ArrayList<BaseDashboardListItem>) {
        val diffUtilResult = DiffUtil.calculateDiff(DashboardDiffUtilCallback(dataList, newData))
        diffUtilResult.dispatchUpdatesTo(this)
        dataList.clear()
        dataList.addAll(newData)
    }
}