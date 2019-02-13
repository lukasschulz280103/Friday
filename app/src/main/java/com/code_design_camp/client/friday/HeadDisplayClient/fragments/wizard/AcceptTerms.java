package com.code_design_camp.client.friday.HeadDisplayClient.fragments.wizard;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroBaseFragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlidePolicy;


public class AcceptTerms extends Fragment implements ISlidePolicy {
    CheckBox accepted_tou;
    CheckBox accepted_pp;
    public AcceptTerms() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static AcceptTerms newInstance() {
        Bundle args = new Bundle();

        AcceptTerms fragment = new AcceptTerms();
        fragment.setArguments(args);
        return fragment;
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
        return accepted_tou.isChecked()&&accepted_pp.isChecked();
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}
