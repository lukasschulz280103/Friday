package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DateWidget extends TextWidget{
    public static final int TYPE_SIMPLE = 0;
    public static final int TYPE_MEDIUM = 1;
    public static final int TYPE_BIG = 2;
    private int relativeTop;
    private ViewGroup parent;
    private int relativeLeft;
    private int type;
    private Context context;
    public DateWidget(@NonNull Context context, @NonNull ViewGroup parent, int left, int top, int type){
        super(context, parent, left, top);
        this.parent = parent;
        this.relativeLeft = left;
        this.relativeTop = top;
        this.type = type;
        this.context = context;
        Log.d("DateWidget","Context is "+context);
    }

    @Override
    public void createWidget() {
        final Date currentTime = Calendar.getInstance().getTime();
        final TextWidget widget = new TextWidget(context,parent,relativeLeft,relativeTop);
        widget.setText(currentTime.toString());
        Timer updater = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                widget.setText(currentTime.toString());
            }
        };
        int delay = 60-Calendar.getInstance().get(Calendar.SECOND);
        updater.scheduleAtFixedRate(task,delay,60000);
        widget.createWidget();
    }
}
