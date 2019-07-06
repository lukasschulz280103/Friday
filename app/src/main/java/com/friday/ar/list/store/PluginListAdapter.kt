package com.friday.ar.list.store

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView

import com.friday.ar.R
import com.friday.ar.plugin.Plugin

class PluginListAdapter(private val context: Context, private val dataList: List<Plugin>) : RecyclerView.Adapter<SimplePluginListItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimplePluginListItemHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.listitem_app_item, parent, false)
        return SimplePluginListItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: SimplePluginListItemHolder, position: Int) {
        val plugin = dataList[position]
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
                        Log.d(LOGTAG, "Uninstalling " + plugin.pluginFile!!.name)
                        if (!plugin.pluginFile!!.delete()) {
                            plugin.pluginFile!!.deleteOnExit()
                        }
                    }
                }
                true
            }
            menu.show()
        }
        holder.size.text = java.lang.Long.toString(plugin.pluginFile!!.length())
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    companion object {
        private const val LOGTAG = "PluginListAdapter"
    }
}
