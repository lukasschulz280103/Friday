package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

public class Widget {
    private int left;
    private int top;
    private ViewGroup parent;
    public Widget(@NonNull ViewGroup parent, int left, int top){
        this.parent = parent;
        this.left = left;
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }
    public void createWidget(View widgetView){
        ConstraintLayout mConstraintLayout  = parent.findViewById(R.id.content_vr_container);
        ConstraintSet set = new ConstraintSet();
        mConstraintLayout.addView(widgetView);
        set.clone(mConstraintLayout);
        set.connect(widgetView.getId(),ConstraintSet.START,mConstraintLayout.getId(),ConstraintSet.START,this.getLeft());
        set.connect(widgetView.getId(),ConstraintSet.TOP,mConstraintLayout.getId(),ConstraintSet.TOP,this.getTop());
        set.applyTo(mConstraintLayout);
        Log.d("WidgetClass","Creating widget from view:"+widgetView);
    }

    public ViewGroup getParent() {
        return parent;
    }
    public interface OnVRViewCreatedCallback {
        void onCreatedWidgets();
    }
}
