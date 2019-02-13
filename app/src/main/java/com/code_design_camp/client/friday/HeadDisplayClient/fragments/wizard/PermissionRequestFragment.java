package com.code_design_camp.client.friday.HeadDisplayClient.fragments.wizard;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlidePolicy;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionRequestFragment extends Fragment implements ISlidePolicy {
    Button grant_perm;
    TextView warning;

    public PermissionRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_permission_request, container, false);
        grant_perm = v.findViewById(R.id.request_perm_btn);
        warning = v.findViewById(R.id.warn);
        grant_perm.setOnClickListener(view -> requestPermissions());
        if(isPolicyRespected()){
            onActivityResult(100,Activity.RESULT_OK,null);
        }
        return v;
    }
    private void requestPermissions(){
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
        getActivity().requestPermissions(permissions,3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100){
            switch (resultCode) {
                case Activity.RESULT_OK:{
                    grant_perm.setEnabled(false);
                    warning.setTextColor(Color.GREEN);
                    warning.setCompoundDrawablesRelative(getActivity().getDrawable(R.drawable.ic_check_green_24dp),null,null,null);
                    warning.setText(R.string.perms_granted);
                }
                case Activity.RESULT_CANCELED:{

                }
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
    @Override
    public boolean isPolicyRespected() {
        return PermissionChecker.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED||
                PermissionChecker.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}
