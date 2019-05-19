package com.friday.ar.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateUtil {
    private static final String LOGTAG = "UpdateUtil";
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference updateRef = database.getReference("version");

    public static void checkForUpdate(Context context) {
        updateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (!context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName.equals(dataSnapshot.getValue().toString())) {
                        NotificationUtil.notifyUpdateAvailable(context, dataSnapshot.getValue().toString());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(LOGTAG, e.getLocalizedMessage(), e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(LOGTAG, "Could not check for update:" + databaseError.getMessage() + "\nDetails:" + databaseError.getDetails(), databaseError.toException());
                Crashlytics.logException(databaseError.toException());
            }
        });
    }
}
