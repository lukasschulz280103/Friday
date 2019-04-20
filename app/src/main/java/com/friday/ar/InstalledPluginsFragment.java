package com.friday.ar;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.list.store.PluginListAdapter;


public class InstalledPluginsFragment extends Fragment {


    public InstalledPluginsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_installed_plugins, container, false);
        RecyclerView list = fragmentView.findViewById(R.id.appList);
        list.setAdapter(new PluginListAdapter(getContext(), null));
        return fragmentView;
    }

}
