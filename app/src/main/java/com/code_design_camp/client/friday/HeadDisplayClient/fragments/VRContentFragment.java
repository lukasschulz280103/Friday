package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableRow;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.view.viewpackage.VRViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class VRContentFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private Context mContext;
    private Activity mActivity;
    ViewGroup parent;
    public VRContentFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public VRContentFragment newInstance(Context context,VRViewHolder holder) {
        VRContentFragment fragment = new VRContentFragment();
        mContext = context;
        File dir = context.getFilesDir();
        File configfile = new File(dir,"vrconfig.json");
        if(!configfile.getAbsoluteFile().exists()){
            try {
                configfile.createNewFile();
                JSONObject rootjson = new JSONObject();
                if(configfile.canWrite()){
                    FileOutputStream writer = new FileOutputStream(configfile);
                    writer.write(rootjson.toString().getBytes());
                }
                else{
                    Toast.makeText(context, "Error: couldn't write VR configuration File", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(context,"Inflating saved workspace",Toast.LENGTH_SHORT).show();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(configfile));
                StringBuilder fileContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line);
                }
                JSONObject avilablejson = new JSONObject(fileContent.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlertDialog.Builder warn = new AlertDialog.Builder(context);
        warn.setTitle("Please read carefully");
        warn.setMessage("Never use friday while driving.");
        warn.setPositiveButton(android.R.string.ok,null);
        warn.create().show();
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
        View fragmentlayout = inflater.inflate(R.layout.fragment_vrcontent, container, false);
        mActivity = getActivity();
        parent = (ViewGroup) fragmentlayout;
        addTextView("TestText",50,50);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return fragmentlayout;
    }
    private void addTextView(String text,int l,int t){
        TextView newText = new TextView(getContext());
        newText.setText(text);
        newText.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams textlayoutparams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textlayoutparams.setMargins(l,t,0,0);
        ConstraintLayout mConstraintLayout  = parent.findViewById(R.id.content_vr_container);
        ConstraintSet set = new ConstraintSet();
        set.connect(newText.getId(),set.LEFT,mConstraintLayout.getId(),set.LEFT);
        set.connect(newText.getId(),set.TOP,mConstraintLayout.getId(),set.TOP);
        newText.setLayoutParams(textlayoutparams);
        mConstraintLayout.addView(newText);
    }
}
