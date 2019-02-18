package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

<<<<<<< HEAD
public class Widget {
=======
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

public class Widget {
    private boolean isCentered = false;
>>>>>>> luke
    private View widgetView;
    private int left;
    private int top;
    private ViewGroup parent;
    Widget(){
        Log.w("Widget","Using an argumentless constructor is not recommended.");
    }

    public View getWidgetView() {
        return widgetView;
    }

    public void setWidgetView(View widgetView) {
        this.widgetView = widgetView;
    }

    Widget(Context context, @NonNull ViewGroup parent, int left, int top){
<<<<<<< HEAD
        this.widgetView = widgetView;
=======
>>>>>>> luke
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
    void createWidget(){
        ConstraintLayout mConstraintLayout  = parent.findViewById(R.id.content_vr_container);
        ConstraintSet set = new ConstraintSet();
        mConstraintLayout.addView(widgetView);
        set.clone(mConstraintLayout);
        set.connect(widgetView.getId(),ConstraintSet.START,mConstraintLayout.getId(),ConstraintSet.START,this.getLeft());
        set.connect(widgetView.getId(),ConstraintSet.TOP,mConstraintLayout.getId(),ConstraintSet.TOP,this.getTop());
<<<<<<< HEAD
=======
        if (isCentered) {
            set.connect(widgetView.getId(), ConstraintSet.END, mConstraintLayout.getId(), ConstraintSet.END, 0);
            set.centerVertically(widgetView.getId(), mConstraintLayout.getId());
            set.centerHorizontally(widgetView.getId(), mConstraintLayout.getId());
        }
>>>>>>> luke
        set.applyTo(mConstraintLayout);
        Log.d("WidgetClass","Creating widget from view:"+widgetView);
    }

<<<<<<< HEAD
=======
    void setIsCentered(boolean isCentered) {
        this.isCentered = isCentered;
    }

>>>>>>> luke
    public ViewGroup getParent() {
        return parent;
    }
    public interface OnVRViewCreatedCallback {
        void onCreatedWidgets();
    }
}
