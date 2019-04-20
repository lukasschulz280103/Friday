package com.friday.ar.store.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.fragments.net.ConnectionFragment;
import com.friday.ar.store.data.WidgetInfo;
import com.friday.ar.store.pager.CardViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.Map;

/**
 * Shows the servers featured fragments.
 *
 * @see com.friday.ar.fragments.store.MainStoreFragment
 */
public class StoreFeaturedFragment extends Fragment {
    public static final String LOGTAG = FridayApplication.LOGTAG_STORE;
    private Context context;
    private FirebaseFirestore fridayStoreFirestore;
    private DocumentReference storeData;
    private ArrayList<CollectionReference> dataList = new ArrayList<>();
    private ViewPager viewPager;
    private OnCompleteListener storeDataLoaded = task -> {
        if (task.isSuccessful()) {
            DocumentSnapshot snap = (DocumentSnapshot) task.getResult();
            ArrayList<Map<String, Object>> data = (ArrayList<Map<String, Object>>) snap.get("list");
            for (Map map : data) {
                WidgetInfo inf = new WidgetInfo();
                CollectionReference collectionReference = fridayStoreFirestore.collection("/store/data/" + map.get("id"));
                dataList.add(collectionReference);
            }
            viewPager.setAdapter(new CardViewPagerAdapter(getFragmentManager(), dataList));
        } else {
            FirebaseFirestoreException e = (FirebaseFirestoreException) task.getException();
            Log.d(LOGTAG, e.getClass().getName());
            ((ConnectionFragment) getParentFragment()).onError(e);
        }
    };

    public StoreFeaturedFragment() {
        FirebaseFirestoreSettings firestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        fridayStoreFirestore = FirebaseFirestore.getInstance();
        fridayStoreFirestore.setFirestoreSettings(firestoreSettings);
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store_featured, container, false);
        viewPager = v.findViewById(R.id.store_main_pager);
        DocumentReference loadedStoreData = fridayStoreFirestore.document("/store/generated/featured-apps/default");
        loadedStoreData.get().addOnCompleteListener(storeDataLoaded);
        return v;
    }

}
