package com.friday.ar.dashboard.internal.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.dashboard.BaseDashboardItem
import com.friday.ar.dashboard.R
import com.friday.ar.pluginsystem.Plugin
import kotlinx.android.synthetic.main.dashboard_base_item.view.*
import org.koin.core.KoinComponent

open class BaseDashboardListItem : Fragment(), BaseDashboardItem {
    companion object {
        private const val LOGTAG = "BaseDashboardListItem"
        fun getInstance(plugin: Plugin): BaseDashboardListItem {
            val fragment = BaseDashboardListItem()
            val args = Bundle()
            args.putSerializable("plugin", plugin)
            fragment.arguments = args
            return fragment
        }
    }

    val plugin = MutableLiveData<Plugin>()

    override fun onCreate(savedInstanceState: Bundle?) {
        plugin.postValue(arguments!!.getSerializable("plugin") as Plugin)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val itemView = inflater.inflate(R.layout.dashboard_base_item, container, false)
        return itemView
    }

    override fun onRequestDataRefresh(newData: BaseDashboardItem): Boolean {
        return false
    }

    override fun onCreateItem() {
        Log.d(LOGTAG, "creating list item $this")
    }

    override fun invalidateItemState() {

    }

    override fun dismiss() {

    }

    override fun onLoadFragment() {

    }


    class BaseDashboardItemViewHolder(view: View) : RecyclerView.ViewHolder(view), KoinComponent {
        val itemIcon = view.itemHeaderIcon
        val itemPluginName = view.itemHeaderPluginName
        val itemSmallText = view.itemHeaderRightText
    }
}