package com.friday.ar.list.store

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.friday.ar.R

class SimplePluginListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var iconView: ImageView = itemView.findViewById(R.id.fileIcon)
    var title: TextView = itemView.findViewById(R.id.name)
    var installStatus: TextView = itemView.findViewById(R.id.installStatus)
    var ratingBar: RatingBar = itemView.findViewById(R.id.rating)
    var size: TextView = itemView.findViewById(R.id.size)
    var overflowMenu: ImageButton = itemView.findViewById(R.id.overflowMenu)
}
