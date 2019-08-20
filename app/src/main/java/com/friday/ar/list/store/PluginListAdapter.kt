package com.friday.ar.list.store

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.R
import com.friday.ar.plugin.Plugin
import com.friday.ar.service.plugin.installer.PluginInstaller
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import extensioneer.isNull


class PluginListAdapter(private val context: Context, private var dataList: MutableList<Plugin>?) : RecyclerView.Adapter<SimplePluginListItemHolder>() {
    companion object {
        private const val LOGTAG = "PluginListAdapter"
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimplePluginListItemHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.listitem_app_item, parent, false)
        return SimplePluginListItemHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SimplePluginListItemHolder, position: Int) {
        val plugin = dataList!![position]
        holder.iconView.setImageURI(plugin.iconURI)
        holder.title.text = plugin.name
        if (plugin.rating != null) {
            holder.ratingBar.rating = plugin.rating!!.starRating
        } else {
            holder.ratingBar.visibility = View.GONE
        }
        holder.installStatus.setText(R.string.store_plugin_status_installed)
        holder.overflowMenu.setOnClickListener { v ->
            val menu = PopupMenu(context, v, Gravity.START)
            menu.inflate(R.menu.store_plugin_item_more)
            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.uninstall_plugin -> {
                        val confirmationDialog = MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.pluginInstaller_uninstall_dialog_title)
                                .setMessage(context.getString(R.string.pluginInstaller_uninstall_dialog_msg, plugin.name))
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(R.string.store_plugin_uninstall) { _, _ ->
                                    PluginInstaller(context).uninstallPlugin(plugin)
                                    notifyItemRemoved(position)
                                }
                                .create()
                        confirmationDialog.show()
                    }
                }
                true
            }
            menu.show()
        }
        holder.size.text = plugin.pluginFile!!.length().toString()
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    fun onRecieveUpdatedData(updatedData: List<Plugin>) {
        dataList.isNull { dataList = updatedData as MutableList<Plugin> }
        val diffResult = DiffUtil.calculateDiff(PluginListDiffUtilCallback(updatedData, dataList!!))

        this.dataList!!.clear()

        this.dataList!!.addAll(updatedData)
        diffResult.dispatchUpdatesTo(this)
    }
}
