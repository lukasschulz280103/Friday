package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

<<<<<<< HEAD
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

>>>>>>> luke
public class WidgetCollection {
    private static final String LOGTAG = "WidgetCollection";
    private static List<String> widgetElementNames = Arrays.asList("TextWidget","DateWidget");
    public static Widget getWidgetFromString(String className, Context context, ViewGroup parent, int left, int top, HashMap<String,Object> arguments){
        Class widgetClass;
        try {
            widgetClass = Class.forName("com.code_design_camp.client.friday.HeadDisplayClient.view."+className);
            switch (className){
                case "TextWidget":{
                    return (Widget) widgetClass.getConstructor(Context.class, ViewGroup.class,int.class,int.class).newInstance(context,parent,left,top);
                }
                case "DateWidget":{
                    return (Widget) widgetClass.getConstructor(Context.class, ViewGroup.class,int.class,int.class,int.class).newInstance(context,parent,left,top,(int) arguments.get("widgetType"));
                }
                default:
                    return null;
            }
        }
        catch (ClassNotFoundException e){
            Log.e(LOGTAG, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Log.e(LOGTAG, e.getMessage(), e);
        } catch (InstantiationException e) {
            Log.e(LOGTAG, e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            Log.e(LOGTAG, e.getMessage(), e);
        } catch (InvocationTargetException e) {
            Log.e(LOGTAG, e.getMessage(), e);
        }
        return null;
    }
}
