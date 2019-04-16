package com.friday.ar.Util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.friday.ar.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

public class LogUtil {

    public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final int VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final boolean BUILD_DEBUG = BuildConfig.DEBUG;
    public static final String BUILD_TYPE = BuildConfig.BUILD_TYPE;
    public static final String DEVICE = Build.DEVICE;
    public static final String MODEL = Build.MODEL;
    public static final String BRAND = Build.BRAND;
    public static final int SDK_VERSION = Build.VERSION.SDK_INT;
    public static final String BASE_OS = Build.VERSION.SDK_INT < 23 ? null : Build.VERSION.BASE_OS;
    public static final String TAGS = Build.TAGS;
    public static final String MANUFACTURER = Build.MANUFACTURER;
    public static final String PRODUCT = Build.PRODUCT;
    public static final long BUID_TIME = Build.TIME;
    private static final String LOGTAG = "LogUtil";

    public static File createDebugInfoFile(Context c, Object... keyValueArgs) {
        File temp_logfiles = c.getCacheDir();
        try {
            File device_info_file = File.createTempFile("log", "json", temp_logfiles);
            Writer out = new FileWriter(device_info_file);
            JSONObject device_info = new JSONObject();
            for (int i = 0; i < keyValueArgs.length; i += 2) {
                Log.d(LOGTAG, device_info.toString());
                if (keyValueArgs[i + 1] != null) {
                    device_info.put((String) keyValueArgs[i], keyValueArgs[i + 1]);
                } else {
                    throw new IllegalArgumentException("keyValueArgs has to be of the following shape: createDebugFile(Context context,key,value,key1,value1)");
                }
            }
            device_info.put("app_build_isDebug", BUILD_DEBUG);
            device_info.put("app_build_type", BUILD_TYPE);
            device_info.put("app_version_name", VERSION_NAME);
            device_info.put("app_version_code", VERSION_CODE);
            device_info.put("freeSpace", temp_logfiles.getFreeSpace());
            device_info.put("device", DEVICE);
            device_info.put("model", MODEL);
            device_info.put("brand", BRAND);
            device_info.put("product", PRODUCT);
            device_info.put("sdkVersion", SDK_VERSION);
            device_info.put("base_os", BASE_OS);
            device_info.put("manufacturer", MANUFACTURER);
            device_info.put("tags", TAGS);
            device_info.put("time", BUID_TIME);
            Log.d("DeviceInfo", device_info.toString());
            out.write(device_info.toString());
            out.close();
            return device_info_file;
        } catch (IOException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
            return null;
        } catch (JSONException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static String fileToString(File logFile) throws FileNotFoundException {
        Scanner sc = new Scanner(logFile);
        String content = "";
        while (sc.hasNext()) {
            content += sc.next();
        }
        Log.d(LOGTAG, "Executed logutil filestostring");
        Log.d(LOGTAG, "DeviceInfoFileContent: " + content);
        return content;
    }
}
