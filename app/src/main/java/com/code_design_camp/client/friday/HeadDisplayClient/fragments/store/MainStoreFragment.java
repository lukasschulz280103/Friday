package com.code_design_camp.client.friday.HeadDisplayClient.fragments.store;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.code_design_camp.client.friday.HeadDisplayClient.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainStoreFragment extends Fragment {
    private ViewPager viewPager;


    public MainStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_store, container, false);
        Toolbar toolbar = v.findViewById(R.id.store_toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().invalidateOptionsMenu();
        return v;
    }

}
