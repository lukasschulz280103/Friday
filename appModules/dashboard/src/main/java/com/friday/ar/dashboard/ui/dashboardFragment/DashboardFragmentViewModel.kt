package com.friday.ar.dashboard.ui.dashboardFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.friday.ar.dashboard.internal.base.BaseDashboardListItem
import com.friday.ar.pluginsystem.Plugin

class DashboardFragmentViewModel : ViewModel() {
    private val dashboardListData = MutableLiveData<ArrayList<BaseDashboardListItem>>()
    val dataList = ArrayList<BaseDashboardListItem>()

    fun getDashBoardListData() = dashboardListData as LiveData<ArrayList<BaseDashboardListItem>>

    init {
        val examplePlugin = Plugin()
        examplePlugin.name = "TestPlugin"
        for (i in 1..12) {
            dataList.add(BaseDashboardListItem.getInstance(examplePlugin))
        }
        dataList.add(BaseDashboardListItem.getInstance(examplePlugin))
        dashboardListData.postValue(dataList)
    }

    fun runRefresh() {
        if (dataList.isNotEmpty()) dataList.remove(dataList[0])
        dashboardListData.postValue(dataList)
    }
}