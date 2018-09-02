package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

class TextWidget extends Widget{
    private TextView newText;
    private String text;
    private int textSize;
    private Color textColor;
    private Context context;
    private ViewGroup parent;
    TextWidget(@NonNull Context context, @NonNull ViewGroup parent, int left, int top, String text){
        super(parent,left,top);
        this.context = context;
        this.setText(text);
        this.parent = parent;
    }
    TextWidget(@NonNull Context context, @NonNull ViewGroup parent,int left,int top){
        super(parent,left,top);
        this.context = context;
        this.setText(text);
        this.parent = parent;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        newText.setText(text);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
    public void createWidget() {
        Widget w = new Widget(parent,getLeft(),getTop());
        newText = new TextView(context);
        newText.setText(text);
        newText.setTextColor(Color.WHITE);
        newText.setId(View.generateViewId());
        newText.setTextSize(20);
        w.createWidget(newText);
    }
}
