package com.code_design_camp.client.friday.HeadDisplayClient.view;

import android.content.Context;
import android.util.Log;
<<<<<<< HEAD
=======
import android.view.Gravity;
>>>>>>> luke
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

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
<<<<<<< HEAD
=======
            if (fileContent.length() == 0) {
                showEmptyScreen();
                return;
            }
>>>>>>> luke
            JSONObject availablejson = new JSONObject(fileContent.toString());
            Iterator<String> jsoniteratable = availablejson.keys();
            while(jsoniteratable.hasNext()){
                String key = jsoniteratable.next();
                Log.d(LOGTAG,availablejson.toString());
<<<<<<< HEAD
                JSONObject widgetJSON = new JSONObject((String) availablejson.get(key));
=======
                JSONObject widgetJSON = (JSONObject) availablejson.get(key);
>>>>>>> luke
                HashMap<String,Object> attributes = new HashMap<>();
                Iterator<String> widgetInterator = widgetJSON.keys();
                while (widgetInterator.hasNext()){
                    String widgetkey = widgetInterator.next();
<<<<<<< HEAD
                    if(widgetkey.equals("left")||widgetkey.equals("right")){
=======
                    if (widgetkey.equals("left") || widgetkey.equals("top")) {
>>>>>>> luke
                        continue;
                    }
                    attributes.put(widgetkey, widgetJSON.get(widgetkey));
                }
<<<<<<< HEAD
                Integer left = (Integer) widgetJSON.get("left");
                Integer top = (Integer) widgetJSON.get("top");
                if(left == null||top == null) {
                    throw new InflateException("Widget object has to define left and top attributes.");
                }
=======
                if (!widgetJSON.has("left") || !widgetJSON.has("top")) {
                    throw new InflateException("Widget object has to define left and top attributes.");
                }
                Integer left = (Integer) widgetJSON.get("left");
                Integer top = (Integer) widgetJSON.get("top");
>>>>>>> luke
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
<<<<<<< HEAD
=======

    private void showEmptyScreen() {
        TextWidget emptyText = new TextWidget(context, parent, 0, 0, "It seems like there are no widgets.\nGo to the layout editor and customize your AR-feeling!");
        emptyText.setIsCentered(true);
        emptyText.setAlignment(Gravity.CENTER_HORIZONTAL);
        emptyText.createWidget();
    }
>>>>>>> luke
    public class InflateException extends Exception{
        String errorMessage;
        InflateException(String widgetErrorMessage){
            super("Couldn't inflate Widget file: "+widgetErrorMessage);
        }
        InflateException(String widgetErrorMessage,String causingElementProperty){

        }
    }
}
