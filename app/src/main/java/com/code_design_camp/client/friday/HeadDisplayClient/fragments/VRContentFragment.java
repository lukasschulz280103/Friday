package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
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
import java.util.Timer;
import java.util.TimerTask;

public class VRContentFragment extends Fragment {
    private static final String LOGTAG = "FllScreenActionActivity";
    private static final int WARNING_MAX_MILLISECONDS = 5000;
    private static final int WARNING_MAX_SECONDS = 5;
    private static AlertDialog.Builder warn;
    private static AlertDialog warndialog;
    private static Context mContext;
    private Activity mActivity;
    static Handler handler;
    ViewGroup parent;
    public static boolean isAlreadyCalled = false;
    public VRContentFragment() {
        // Required empty public constructor
    }
    static Runnable updateWarnDialog = new Runnable() {
        int currentsec = 5;
        @Override
        public void run() {
            Button posBtn = warndialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Log.d(LOGTAG,"currentsec = "+currentsec);
            if (currentsec == 0) {
                posBtn.setEnabled(true);
                posBtn.setText(mContext.getResources().getString(android.R.string.ok));
                return;
            }
            else {
                posBtn.setText(mContext.getResources().getString(R.string.ok_btn_seconds,currentsec));
                posBtn.setEnabled(false);
            }
            handler.postDelayed(updateWarnDialog, 1000);
            currentsec -= 1;
        }
    };
    public static VRContentFragment newInstance(final Context context, VRViewHolder holder) {
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
        warn = new AlertDialog.Builder(context);
        warn.setTitle("Please read carefully");
        warn.setMessage("Never use friday while driving.");
        warn.setPositiveButton(android.R.string.ok,null);
        Log.d(LOGTAG,"Showing warn dialog");
        warndialog = warn.create();
        warndialog.show();
        handler = new Handler();
        updateWarnDialog.run();
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
        addTextView("Beispiel Text",100,150);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return fragmentlayout;
    }
    private void addTextView(String text,int left,int top){
        /*if(isAlreadyCalled){
            return;
        }

        isAlreadyCalled = true;*/
        TextView newText = new TextView(getContext());
        newText.setText(text);
        newText.setTextColor(Color.WHITE);
        newText.setId(View.generateViewId());
        newText.setTextSize(20);
        ConstraintLayout mConstraintLayout  = parent.findViewById(R.id.content_vr_container);
        Log.d(LOGTAG,"mConstraintLayout = " + mConstraintLayout.toString());
        Log.d(LOGTAG,"newTextID = " + newText.getId());
        Log.d(LOGTAG,"mConstraintLayoutID = " + mConstraintLayout.getId());
        ConstraintSet set = new ConstraintSet();
        mConstraintLayout.addView(newText);

        set.clone(mConstraintLayout);
        set.connect(newText.getId(),ConstraintSet.START,mConstraintLayout.getId(),ConstraintSet.START,left);
        set.connect(newText.getId(),ConstraintSet.TOP,mConstraintLayout.getId(),ConstraintSet.TOP,top);
        set.applyTo(mConstraintLayout);
    }
    private void addClockView(int left,int top){

    }
}