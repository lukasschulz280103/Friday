package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class WidgetInflater {
    private static final String LOGTAG = "WidgetInflater";
    private static final String WIDGETERRTAG = "WidgetInflateError";
    private Context context;
    private ViewGroup parent;
    public WidgetInflater(Context ApplicationContext,ViewGroup parentViewGroup){
        this.context = ApplicationContext;
        this.parent = parentViewGroup;
    }
    public void fromFile(File configfile){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configfile));
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
            JSONObject availablejson = new JSONObject(fileContent.toString());
            Iterator<String> jsoniteratable = availablejson.keys();
            while(jsoniteratable.hasNext()){
                String key = jsoniteratable.next();
                Log.d(LOGTAG,availablejson.toString());
                JSONObject widgetJSON = new JSONObject((String) availablejson.get(key));
                HashMap<String,Object> attributes = new HashMap<>();
                Iterator<String> widgetInterator = widgetJSON.keys();
                while (widgetInterator.hasNext()){
                    String widgetkey = widgetInterator.next();
                    if(widgetkey.equals("left")||widgetkey.equals("right")){
                        continue;
                    }
                    attributes.put(widgetkey, widgetJSON.get(widgetkey));
                }
                Integer left = (Integer) widgetJSON.get("left");
                Integer top = (Integer) widgetJSON.get("top");
                if(left == null||top == null) {
                    throw new InflateException("Widget object has to define left and top attributes.");
                }
                Widget w = inflateWidget(key,left,top,attributes);
                w.createWidget();
            }

        } catch (IOException e) {
            Log.e(WIDGETERRTAG,e.getLocalizedMessage());
        } catch (JSONException e) {
            Log.e(WIDGETERRTAG,e.getLocalizedMessage());
        } catch (InflateException e) {
            Log.e(WIDGETERRTAG,e.getLocalizedMessage());
        }

    }
    private Widget inflateWidget(String WidgetName, int left, int top, HashMap<String,Object> attributes) throws InflateException {
        Log.d(LOGTAG,"Inflating widget from string:"+WidgetName);
        return WidgetCollection.getWidgetFromString(WidgetName,context,parent,left,top,attributes);
    }
    public class InflateException extends Exception{
        String errorMessage;
        InflateException(String widgetErrorMessage){
            super("Couldn't inflate Widget file: "+widgetErrorMessage);
        }
        InflateException(String widgetErrorMessage,String causingElementProperty){

        }
    }
}
