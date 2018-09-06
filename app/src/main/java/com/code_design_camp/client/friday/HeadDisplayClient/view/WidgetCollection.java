package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.view.ViewGroup;

import com.code_design_camp.client.friday.HeadDisplayClient.LogFormat.StacktraceFormatter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WidgetCollection {
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
            StacktraceFormatter.logStackTrace(e);
        } catch (IllegalAccessException e) {
            StacktraceFormatter.logStackTrace(e);
        } catch (InstantiationException e) {
            StacktraceFormatter.logStackTrace(e);
        } catch (NoSuchMethodException e) {
            StacktraceFormatter.logStackTrace(e);
        } catch (InvocationTargetException e) {
            StacktraceFormatter.logStackTrace(e);
        }
        return null;
    }
}
