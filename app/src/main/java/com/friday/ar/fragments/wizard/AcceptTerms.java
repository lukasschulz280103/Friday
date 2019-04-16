package com.friday.ar.fragments.wizard;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.friday.ar.R;
import com.github.paolorotolo.appintro.ISlidePolicy;


public class AcceptTerms extends Fragment implements ISlidePolicy {
    CheckBox accepted_tou;
    CheckBox accepted_pp;

    public AcceptTerms() {
        // Required empty public constructor
    }

    public static AcceptTerms newInstance() {
        Bundle args = new Bundle();

        AcceptTerms fragment = new AcceptTerms();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_accept_terms, container, false);
        ScrollView tou_scroll = v.findViewById(R.id.tou_scroll);
        TextView tou_text = v.findViewById(R.id.tou_text);
        accepted_tou = v.findViewById(R.id.check_accept_tou);
        accepted_pp = v.findViewById(R.id.check_accept_pp);
        return v;
    }

    @Override
    public boolean isPolicyRespected() {
        return accepted_tou.isChecked() && accepted_pp.isChecked();
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}
