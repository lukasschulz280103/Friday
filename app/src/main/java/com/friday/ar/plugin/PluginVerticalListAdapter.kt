package com.friday.ar.plugin

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.R
import com.friday.ar.plugin.file.ZippedPluginFile
import java.io.File
import java.util.*

//TODO: Add functionality that makes an item selectable
class PluginVerticalListAdapter(private val context: Activity, private val dataList: ArrayList<ZippedPluginFile>) : RecyclerView.Adapter<PluginViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PluginViewHolder {
        return PluginViewHolder(LayoutInflater.from(context).inflate(R.layout.file_selector_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: PluginViewHolder, position: Int) {
        val file = dataList[position]
        holder.title.text = file.file.name
        holder.path.text = file.file.name
        holder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_twotone_insert_drive_file_24px))
        holder.root.setOnClickListener { v ->
            val resultIntent = Intent()
            resultIntent.data = Uri.fromFile(File(file.file.name))
            context.setResult(RESULT_OK, resultIntent)
            context.findViewById<View>(R.id.select_file).isEnabled = true
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

