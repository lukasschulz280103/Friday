package com.friday.ar.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateUtil {
    private Context context;
    private String versionNameServer;
    private PackageInfo pm;
    private OnStateChangedListener listener;

    public UpdateUtil(final Context context) {
        this.context = context;
        try {
            pm = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference updateref = db.getReference("version");
        updateref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onStateChanged((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onStateChanged(null);
            }
        });
    }

    public void setListener(OnStateChangedListener listener) {
        this.listener = listener;
    }

    public interface OnStateChangedListener {
        void onStateChanged(String versionNumberServer);
    }
}
