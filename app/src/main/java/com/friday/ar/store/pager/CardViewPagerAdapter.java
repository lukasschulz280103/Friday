package com.friday.ar.store.pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.friday.ar.store.ui.AppCardFragment;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class CardViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<CollectionReference> dataList;

    public CardViewPagerAdapter(FragmentManager fm, ArrayList<CollectionReference> data) {
        super(fm);
        this.dataList = data;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        AppCardFragment fragment = new AppCardFragment(dataList.get(position));
        return fragment;
    }
}
