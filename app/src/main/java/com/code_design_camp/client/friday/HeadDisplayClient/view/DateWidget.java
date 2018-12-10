package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

/*
 * (C) Copyright 2018 Lukas Faber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Lukas Faber
 */

public class DateWidget extends TextWidget{
    public static final int TYPE_SIMPLE = 0;
    public static final int TYPE_EXPANDED = 1;
    public static final int TYPE_FULL = 2;
    public static final int TYPE_DATE_DEFAULT = 3;
    //These are of type String to make the formatting easier (and we don't need these to be datatype int)
    private String HOUR;
    private String MINUTE;
    private String SECOND;
    private String DAY;
    private String MONTH;
    private int YEAR;
    private String DAY_NAME;
    private String MONTH_NAME;
    private Calendar timecalendar;
    private final Handler handler = new Handler();
    private int relativeTop;
    private ViewGroup parent;
    private TextWidget widget;
    private int relativeLeft;
    private int type;
    private Context context;
    private Resources res;
    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            MINUTE = Integer.toString(timecalendar.get(Calendar.MINUTE));
            HOUR = Integer.toString(timecalendar.get(Calendar.HOUR_OF_DAY));
            SECOND = Integer.toString(timecalendar.get(Calendar.SECOND));
            DAY  = Integer.toString(timecalendar.get(Calendar.DAY_OF_MONTH));
            MONTH = Integer.toString(timecalendar.get(Calendar.MONTH));
            DAY_NAME = timecalendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.GERMAN);
            MONTH_NAME = timecalendar.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.GERMAN);
            if(MINUTE.length() < 2){
                MINUTE = "0" + MINUTE;
            }
            if(HOUR.length() < 2){
                HOUR = "0" + HOUR;
            }
            if(SECOND.length() < 2){
                SECOND = "0" + SECOND;
            }
        }
    };
    public DateWidget(@NonNull Context context, @NonNull ViewGroup parent, int left, int top, int type){
        super(context, parent, left, top);
        this.parent = parent;
        this.relativeLeft = left;
        this.relativeTop = top;
        this.type = type;
        this.context = context;
        this.res = context.getResources();
        this.timecalendar = new GregorianCalendar();
        Log.d("DateWidget","Context is "+context);
    }
    private final TimerTask updateMinuteTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(updateTime);
        }
    };
    private final TimerTask updateHourTask = new TimerTask() {
        @Override
        public void run() {
            handler.post(updateTime);
        }
    };
    private final TimerTask updateSecondsTask = new TimerTask() {
        @Override
        public void run() {

        }
    };
    public int getRelativeTop() {
        return relativeTop;
    }

    public void setRelativeTop(int relativeTop) {
        this.relativeTop = relativeTop;
    }

    @Override
    public ViewGroup getParent() {
        return parent;
    }

    public void setParent(ViewGroup parent) {
        this.parent = parent;
    }

    public int getRelativeLeft() {
        return relativeLeft;
    }

    public void setRelativeLeft(int relativeLeft) {
        this.relativeLeft = relativeLeft;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public void start(){
        Timer updaterMinute = new Timer();
        Timer updaterHour = new Timer();
        Timer updateSeconds = new Timer();
        int delaymin = 60-Calendar.getInstance().get(Calendar.SECOND);
        int delayhours = 60-timecalendar.get(Calendar.MINUTE);
        int delayseconds = 1000-timecalendar.get(Calendar.MILLISECOND);
        YEAR = timecalendar.get(Calendar.YEAR);
        updaterMinute.scheduleAtFixedRate(updateMinuteTask,delaymin,60000);
        updaterHour.scheduleAtFixedRate(updateHourTask,delayhours,360000);
        updateSeconds.scheduleAtFixedRate(updateSecondsTask,delayseconds,1000);
    }
    @Override
    public void createWidget() {
        updateTime.run();
        start();
        if (type == TYPE_SIMPLE) {
            super.setText(res.getString(R.string.string_time,HOUR,MINUTE));
        } else if (type == TYPE_EXPANDED) {
            super.setText(res.getString(R.string.string_time_expanded,DAY_NAME,MINUTE,SECOND));
        } else if (type == TYPE_FULL) {
            Date date = new Date();
            super.setText(date.toString());
        } else if (type == TYPE_DATE_DEFAULT){
            super.setText(res.getString(R.string.string_date_default,DAY,MONTH,YEAR));
        }
        super.createWidget();
    }
}
