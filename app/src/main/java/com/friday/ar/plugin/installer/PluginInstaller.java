package com.friday.ar.plugin.installer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.friday.ar.Constant;
import com.friday.ar.R;
import com.friday.ar.plugin.file.Manifest;
import com.friday.ar.plugin.file.PluginFile;
import com.friday.ar.plugin.file.ZippedPluginFile;
import com.friday.ar.plugin.security.PluginVerifier;
import com.friday.ar.plugin.security.VerificationSecurityException;

import net.lingala.zip4j.exception.ZipException;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public class PluginInstaller {
    private static final String LOGTAG = "PluginInstaller";
    private Context context;

    public PluginInstaller(Context context) {
        this.context = context;
    }

    public void installFrom(ZippedPluginFile pluginDir) throws IOException, IllegalFileException {
        File zippedPluginDefaultFile = pluginDir.getFile();
        try {
            pluginDir.extractAll(Constant.getPluginCacheDir(context).getPath());
            PluginFile extractionTargetDirectory = new PluginFile(Constant.getPluginCacheDir(context, zippedPluginDefaultFile.getName().replace(".fpl", "")).getPath());
            PluginVerifier.verify(extractionTargetDirectory, true);
            extractionTargetDirectory.renameTo(Constant.getPluginDir(context, zippedPluginDefaultFile.getName().replace(".fpl", "")));
        } catch (JSONException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
            Notification notification = new NotificationCompat.Builder(context, Constant.NOTIF_CHANNEL_INSTALLER_ID)
                    .setContentTitle(context.getString(R.string.pluginInstaller_error_installation_failed))
                    .setContentText(context.getString(R.string.pluginInstaller_error_could_not_parse))
                    .setContentInfo(zippedPluginDefaultFile.getName())
                    .setSubText(context.getString(R.string.pluginInstaller_name))
                    .setCategory(NotificationCompat.CATEGORY_ERROR)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.ic_twotone_error_outline_24px)
                    .setTicker(context.getString(R.string.pluginInstaller_error_could_not_parse))
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constant.NotificationIDs.NOTIFICATION_INSTALL_ERROR, notification);
            return;
        } catch (VerificationSecurityException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
            Notification notification = new NotificationCompat.Builder(context, Constant.NOTIF_CHANNEL_INSTALLER_ID)
                    .setContentTitle(context.getString(R.string.pluginInstaller_error_installation_failed))
                    .setContentText(context.getString(R.string.pluginInstaller_error_manifest_security))
                    .setContentInfo(zippedPluginDefaultFile.getName())
                    .setSubText(context.getString(R.string.pluginInstaller_name))
                    .setCategory(NotificationCompat.CATEGORY_ERROR)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.ic_twotone_error_outline_24px)
                    .setTicker(context.getString(R.string.pluginInstaller_error_could_not_parse))
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constant.NotificationIDs.NOTIFICATION_INSTALL_ERROR, notification);
            return;
        } catch (ZipException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        }
        new File(zippedPluginDefaultFile.getPath()).renameTo(new File(context.getFilesDir() + "/plugin/" + zippedPluginDefaultFile.getName()));
        try {
            Manifest succesPluginManifest = new PluginFile(Constant.getPluginDir(context, zippedPluginDefaultFile.getName().replace(".fpl", "")).getPath()).getManifest();
            Notification notification = new NotificationCompat.Builder(context, Constant.NOTIF_CHANNEL_INSTALLER_ID)
                    .setContentTitle(context.getString(R.string.pluginInstaller_succes_install_title))
                    .setContentText(zippedPluginDefaultFile.getName())
                    .setSubText(context.getString(R.string.pluginInstaller_name))
                    .setCategory(NotificationCompat.CATEGORY_STATUS)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_twotone_save_alt_24px)
                    .setTicker(context.getString(R.string.pluginInstaller_success_ticker_text, succesPluginManifest.getPluginName()))
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constant.NotificationIDs.NOTIFICATION_INSTALL_SUCCESS, notification);
        } catch (JSONException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        }
    }

    public class IllegalFileException extends IllegalStateException {
        IllegalFileException(String message) {
            super(message);
        }
    }
}
