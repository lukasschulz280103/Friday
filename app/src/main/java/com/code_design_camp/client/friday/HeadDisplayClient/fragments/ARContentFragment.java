package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.google.ar.sceneform.ux.ArFragment;

import androidx.annotation.NonNull;

public class ARContentFragment extends ArFragment {
    private static final String LOGTAG = "FllScreenActionActivity";
    private static ViewGroup parent;
    private Activity mActivity;

    public ARContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentlayout = inflater.inflate(R.layout.fragment_vrcontent, container, false);
        mActivity = getActivity();
        parent = (ViewGroup) fragmentlayout;
        Log.d("VRFragment", "Context is " + mActivity);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return fragmentlayout;
    }
}