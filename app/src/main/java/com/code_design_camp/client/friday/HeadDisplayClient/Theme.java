package com.code_design_camp.client.friday.HeadDisplayClient;

import android.content.Context;

import androidx.core.content.ContextCompat;

public class Theme {
    public static int[] colors = new int[]{
            R.color.warn_red,
            R.color.colorPrimary,
            R.color.colorSecondary,
            R.color.colorAccent,
            R.color.colorError,
            android.R.color.black,
            android.R.color.holo_orange_dark
    };

    public Theme() {

    }

    public static int getColorForPos(Context c, int pos) {
        return ContextCompat.getColor(c, colors[pos]);
    }

}