package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.view.Widget;
import com.code_design_camp.client.friday.HeadDisplayClient.view.WidgetInflater;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class VRContentFragment extends Fragment implements Widget.OnVRViewCreatedCallback{
    private static final String LOGTAG = "FllScreenActionActivity";
    private static AlertDialog.Builder warn;
    private static AlertDialog warndialog;
    private static Context mContext;
    private Activity mActivity;
    private static Handler handler;
    private static ViewGroup parent;
    public VRContentFragment() {
        // Required empty public constructor
    }
    private static Runnable updateWarnDialog = new Runnable() {
        int currentsec = 5;
        @Override
        public void run() {
            Button posBtn = warndialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Log.d(LOGTAG,"currentsec = "+currentsec);
            if (currentsec == 0) {
                posBtn.setEnabled(true);
                posBtn.setText(mContext.getResources().getString(R.string.dismiss_btn));
                return;
            }
            else {
                posBtn.setText(mContext.getResources().getString(R.string.dismiss_btn_seconds,currentsec));
                posBtn.setEnabled(false);
            }
            handler.postDelayed(updateWarnDialog, 1000);
            currentsec -= 1;
        }
    };

    public static VRContentFragment newInstance(final Context context) {
        VRContentFragment fragment = new VRContentFragment();
        mContext = context;
        File dir = context.getFilesDir();
        File configfile = new File(dir,"vrconfig.json");
        //Creating file with example widget
        if(!configfile.getAbsoluteFile().exists()){
            try {
                configfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(context,"Inflating saved workspace",Toast.LENGTH_SHORT).show();
            WidgetInflater inflater = new WidgetInflater(mContext,parent);
            inflater.fromFile(configfile);
        }

        warn = new AlertDialog.Builder(context);
        warn.setTitle(R.string.dialog_warn_title);
        warn.setMessage(R.string.tou_content);
        warn.setPositiveButton(android.R.string.ok,null);
        warn.setNegativeButton(R.string.warning_decline, (dialogInterface, i) -> ((FragmentActivity) mContext).finish());
        warn.setCancelable(false);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentlayout = inflater.inflate(R.layout.fragment_vrcontent, container, false);
        mActivity = getActivity();
        parent = (ViewGroup) fragmentlayout;
        Log.d("VRFragment","Context is "+mActivity);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return fragmentlayout;
    }

    @Override
    public void onCreatedWidgets() {
        Log.d("VRFragment","Created widgets");
    }
}