package com.friday.ar.fragments.store;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.friday.ar.R;
import com.friday.ar.fragments.net.ConnectionFragment;
import com.friday.ar.fragments.net.OnConnectionStateChangedListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainStoreFragment extends ConnectionFragment implements OnConnectionStateChangedListener {


    public MainStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("storefragment", "creating store view");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_store, container, false);
        Toolbar toolbar = v.findViewById(R.id.store_toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().invalidateOptionsMenu();
        addFragment(R.id.store_featured_fragment_container, new StoreFeaturedFragment());
        return v;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onError(Exception e) {
        Log.e("StoreError", "Error occured at store");
        getFragmentManager().beginTransaction()
                .add(R.id.store_frag_container, new ErrorFragment((FirebaseFirestoreException) e))
                .remove(this)
                .commitAllowingStateLoss();
    }

    private void addFragment(@IdRes int containerId, Fragment newFragment) {
        getChildFragmentManager().beginTransaction()
                .replace(containerId, newFragment)
                .commitAllowingStateLoss();
    }
}
