package com.code_design_camp.client.friday.HeadDisplayClient.store.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.code_design_camp.client.friday.HeadDisplayClient.FridayApplication;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ErrorDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.net.ConnectionFragment;
import com.code_design_camp.client.friday.HeadDisplayClient.store.data.WidgetInfo;
import com.code_design_camp.client.friday.HeadDisplayClient.store.pager.CardViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

/**
 * Shows the servers featured fragments.
 * @see com.code_design_camp.client.friday.HeadDisplayClient.fragments.store.MainStoreFragment
 */
public class StoreFeaturedFragment extends Fragment {
    public static final String LOGTAG = FridayApplication.LOGTAG_STORE;
    private Context context;
    private FirebaseFirestore storedatafs = FirebaseFirestore.getInstance();
    private DocumentReference storedata;
    private ArrayList<CollectionReference> dataList = new ArrayList<>();
    private ViewPager viewPager;
    private OnCompleteListener storeDataLoaded = task -> {
        if (task.isSuccessful()) {
            DocumentSnapshot snap = (DocumentSnapshot) task.getResult();
            ArrayList<Map<String, Object>> data = (ArrayList<Map<String, Object>>) snap.get("list");
            for (Map map : data) {
                WidgetInfo inf = new WidgetInfo();
                CollectionReference collectionReference = storedatafs.collection("/store/data/" + map.get("id"));
                dataList.add(collectionReference);
            }
            viewPager.setAdapter(new CardViewPagerAdapter(getFragmentManager(), dataList));
        } else {
            FirebaseFirestoreException e = (FirebaseFirestoreException) task.getException();
            Log.d(LOGTAG,e.getClass().getName());
            ((ConnectionFragment) getParentFragment()).onError(e);
        }
    };

    public StoreFeaturedFragment() {
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
        DocumentReference loadedStoreData = storedatafs.document("/store/generated/featured-apps/default");
        loadedStoreData.get().addOnCompleteListener(storeDataLoaded);
        return v;
    }

}