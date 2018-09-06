package com.code_design_camp.client.friday.HeadDisplayClient.LogFormat;

import android.util.Log;

public class StacktraceFormatter {
    public static void logStackTrace(Exception e){
        StringBuilder error = new StringBuilder();
        error.append(e.getClass()+"|"+e.getCause().getLocalizedMessage()+":"+e.getMessage()+"\n");
        for(StackTraceElement elem:e.getStackTrace()){
            error.append("in "+elem.getFileName()+":"+elem.getLineNumber()+": "+elem.getMethodName()+"()/"+elem.isNativeMethod()+"\n");
        }
        Log.e("RuntimeError",error.toString());
    }
}
