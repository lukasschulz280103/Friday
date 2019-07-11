package com.friday.ar.util.list

/**
 * This class is likely to be used to communicate between RecyclerViews and fragments/activities
 */
interface OnDataUpdateListener<T> {
    fun onUpdate(data: List<T>)
}