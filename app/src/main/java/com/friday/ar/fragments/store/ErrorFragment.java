package com.friday.ar.fragments.store;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.friday.ar.R;
import com.friday.ar.Util.FireStoreCodeInterpreter;
import com.google.firebase.firestore.FirebaseFirestoreException;


/**
 * Error Fragment which shows an error page to the user.
 *
 * @see MainStoreFragment, com.friday.HeadDisplayClient.store.fragments.StoreFeaturedFragment
 */
public class ErrorFragment extends Fragment {
    private FirebaseFirestoreException e;
    private Context context;


    public ErrorFragment(FirebaseFirestoreException e) {
        this.e = e;
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
        // Inflate the layout for this fragment
        FireStoreCodeInterpreter errInterpreter = new FireStoreCodeInterpreter(getContext(), e);
        View v = inflater.inflate(R.layout.fragment_error, container, false);
        Button retry = v.findViewById(R.id.retry);
        ((TextView) v.findViewById(R.id.errTitle)).setText(getString(R.string.loading_err_title, String.valueOf(errInterpreter.getCode())));
        ((TextView) v.findViewById(R.id.subtitle_err)).setText(errInterpreter.getMessage());
        ((TextView) v.findViewById(R.id.fullMessage)).setText(errInterpreter.getExceptionMessage());
        retry.setOnClickListener((view) -> getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .add(R.id.store_frag_container, new MainStoreFragment())
                .remove(this)
                .commit());
        return v;
    }

}
