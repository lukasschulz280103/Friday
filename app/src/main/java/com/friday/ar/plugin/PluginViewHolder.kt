package com.friday.ar.plugin

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.R

class PluginViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var root: LinearLayout = itemView.findViewById(R.id.itemRootLayout)
    internal var title: TextView = itemView.findViewById(R.id.fileName)
    internal var path: TextView = itemView.findViewById(R.id.fileSize)
    internal var icon: ImageView = itemView.findViewById(R.id.fileIcon)

}
