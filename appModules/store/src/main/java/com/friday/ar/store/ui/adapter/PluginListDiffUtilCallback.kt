package com.friday.ar.store.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.friday.ar.pluginsystem.Plugin

class PluginListDiffUtilCallback(private val newData: List<Plugin>, private val oldData: List<Plugin>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newData[newItemPosition] == oldData[oldItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}