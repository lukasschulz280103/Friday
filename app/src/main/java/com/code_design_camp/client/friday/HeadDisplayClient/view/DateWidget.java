package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;

public class DateWidget extends TextWidget{
    public static final int TYPE_SIMPLE = 0;
    public static final int TYPE_MEDIUM = 1;
    public static final int TYPE_BIG = 2;
    private int relativeTop;
    private ConstraintLayout parent;
    private int relativeLeft;
    private int type;
    public DateWidget(ConstraintLayout parent,int left, int top, int type){
        this.parent = parent;
        this.relativeLeft = left;
        this.relativeTop = top;
        this.type = type;
    }
}
