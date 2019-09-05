package com.friday.ar.dashboard


interface BaseDashboardItem {
    /**
     * Called when user refreshes dashboard.
     * @param newData new data
     * @return return a boolean if data has changed
     */
    fun onRequestDataRefresh(newData: BaseDashboardItem): Boolean

    fun onCreateItem()

    /**
     * invalidate state of this item (refreshes item)
     */
    fun invalidateItemState()

    /**
     * dismiss this item; removes item from dashboard
     */
    fun dismiss()

    fun onLoadFragment()
}