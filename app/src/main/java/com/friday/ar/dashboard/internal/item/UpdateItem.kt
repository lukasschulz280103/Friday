package com.friday.ar.dashboard.internal.item

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.R
import com.friday.ar.dashboard.DashboardListItem
import com.friday.ar.ui.InfoActivity
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.dashboard_update.view.*

class UpdateItem : DashboardListItem() {

    lateinit var context: Context
    override fun getViewHolder(context: Context, parent: ViewGroup): RecyclerView.ViewHolder {
        this.context = context
        val view = LayoutInflater.from(context).inflate(R.layout.dashboard_update, parent, false)
        return UpdateItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder) {
        val updateViewHolder = viewHolder as UpdateItemViewHolder
        updateViewHolder.updateButton.setOnClickListener {
            context.startActivity(Intent(context, InfoActivity::class.java))
        }
    }

    class UpdateItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val updateButton: MaterialButton = itemView.update_now
    }

    override fun onRequestDataRefresh(newData: DashboardListItem): Boolean {
        return false
    }
}