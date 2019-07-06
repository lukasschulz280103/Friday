package com.friday.ar.store.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.friday.ar.store.ui.AppCardFragment
import com.google.firebase.firestore.CollectionReference
import java.util.*

class CardViewPagerAdapter(fm: FragmentManager, private val dataList: ArrayList<CollectionReference>) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return dataList.size
    }


    override fun getItem(position: Int): Fragment {
        return AppCardFragment(dataList[position])
    }
}
