package com.code_design_camp.client.friday.HeadDisplayClient.fragments.templateClasses;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.store.MainStoreFragment;
import com.google.firebase.firestore.FirebaseFirestoreException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends Fragment {
    private FirebaseFirestoreException e;


    public ErrorFragment(FirebaseFirestoreException e) {
        this.e = e;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_error, container, false);
        Button retry = v.findViewById(R.id.retry);
        ((TextView) v.findViewById(R.id.errTitle)).setText(getString(R.string.loading_err_title,String.valueOf(e.getCode().value())));
        ((TextView) v.findViewById(R.id.subtitle_err)).setText(e.getLocalizedMessage());
        retry.setOnClickListener((view) -> getFragmentManager().beginTransaction()
                .add(R.id.store_frag_container,new MainStoreFragment())
                .remove(this)
                .commit());
        return v;
    }

}
