package com.code_design_camp.client.friday.HeadDisplayClient.fragments.wizard;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.WizardActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroBase;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlidePolicy;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionRequestFragment extends Fragment implements ISlidePolicy {
    String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
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
            onRequestPermissionsResult(100,permissions,null);
        }
        else{

        }
        return v;
    }
    private void requestPermissions(){
        requestPermissions(permissions,3);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int[] targetGrantResults = new int[]{PermissionChecker.PERMISSION_GRANTED,PermissionChecker.PERMISSION_GRANTED};
        if(requestCode == 3){
            if(!targetGrantResults.equals(grantResults)) {
                grant_perm.setEnabled(false);
                warning.setTextColor(Color.GREEN);
                warning.setCompoundDrawablesWithIntrinsicBounds(getActivity().getDrawable(R.drawable.ic_check_green_24dp), null, null, null);
                warning.setText(R.string.perms_granted);
            }
            else{

            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
