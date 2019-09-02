package com.friday.ar.store.pager

import android.os.Bundle
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
        val card = AppCardFragment()
        val bundle = Bundle()
        bundle.putString("path", dataList[position].path)
        card.arguments = bundle
        return card
    }
}
