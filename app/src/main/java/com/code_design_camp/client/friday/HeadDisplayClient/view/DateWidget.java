package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.view.SupportActionModeWrapper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class DateWidget extends TextWidget{
    public static final int TYPE_SIMPLE = 0;
    public static final int TYPE_MEDIUM = 1;
    public static final int TYPE_BIG = 2;
    public static String HOUR;
    public static String MINUTE;
    private int relativeTop;
    private ViewGroup parent;
    final TextWidget widget;
    private int relativeLeft;
    private int type;
    private Context context;
    private Resources res;
    Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            Calendar time = Calendar.getInstance();
            MINUTE = Integer.toString(time.get(Calendar.MINUTE));
            HOUR = Integer.toString(time.get(Calendar.HOUR_OF_DAY));
            if(MINUTE.length() < 2){
                MINUTE = "0" + MINUTE;
            }
            if(HOUR.length() < 2){
                HOUR = "0" + HOUR;
            }
            widget.setText(res.getString(R.string.string_time,HOUR,MINUTE));
        }
    };
    public DateWidget(@NonNull Context context, @NonNull ViewGroup parent, int left, int top, int type){
        super(context, parent, left, top);
        widget = new TextWidget(context,parent,left,top);
        this.parent = parent;
        this.relativeLeft = left;
        this.relativeTop = top;
        this.type = type;
        this.context = context;
        this.res = context.getResources();
        Log.d("DateWidget","Context is "+context);
    }

    @Override
    public void createWidget() {
        final Calendar time = Calendar.getInstance();
        MINUTE = Integer.toString(time.get(Calendar.MINUTE));
        HOUR = Integer.toString(time.get(Calendar.HOUR_OF_DAY));
        widget.setText(res.getString(R.string.string_time,HOUR,MINUTE));
        Timer updaterMinute = new Timer();
        Timer updaterHour = new Timer();
        final Handler handler = new Handler();
        TimerTask updateMinuteTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(updateTime);
            }
        };
        TimerTask updateHourTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(updateTime);
            }
        };
        int delaymin = 60-Calendar.getInstance().get(Calendar.SECOND);
        int delayhours = 60-Integer.valueOf(MINUTE);
        updaterMinute.scheduleAtFixedRate(updateMinuteTask,delaymin,60000);
        updaterHour.scheduleAtFixedRate(updateHourTask,delayhours,360000);
        widget.createWidget();
    }
}
