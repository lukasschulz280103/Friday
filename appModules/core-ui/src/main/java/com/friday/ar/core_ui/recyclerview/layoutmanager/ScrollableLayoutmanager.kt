package com.friday.ar.core_ui.recyclerview.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class ScrollableLayoutmanager(context: Context) : LinearLayoutManager(context) {
    val isScrollingEnabled: Boolean = true
    override fun canScrollVertically(): Boolean {
        return isScrollingEnabled && super.canScrollVertically()
    }
}