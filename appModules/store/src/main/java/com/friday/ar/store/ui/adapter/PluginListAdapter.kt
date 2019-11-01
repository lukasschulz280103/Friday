package com.friday.ar.store.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.file.PluginFile
import com.friday.ar.pluginsystem.service.installer.PluginInstaller
import com.friday.ar.store.R
import com.friday.ar.store.ui.StoreDetailActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PluginListAdapter(private val context: Context) : RecyclerView.Adapter<SimplePluginListItemHolder>() {
    companion object {
        private const val LOGTAG = "PluginListAdapter"
    }

    private var dataList = emptyList<Plugin>()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimplePluginListItemHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.listitem_app_item, parent, false)
        return SimplePluginListItemHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SimplePluginListItemHolder, position: Int) {
        val plugin = dataList[position]
        holder.title.text = plugin.name
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
        holder.rootView.setOnClickListener {
            context.startActivity(Intent(context, StoreDetailActivity::class.java))
        }
        holder.size.text = PluginFile(plugin.pluginFileUri).length().toString()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun onRecieveUpdatedData(updatedData: List<Plugin>) {
        val diffResult = DiffUtil.calculateDiff(PluginListDiffUtilCallback(updatedData, dataList))

        diffResult.dispatchUpdatesTo(this)

        dataList = updatedData
    }
}
