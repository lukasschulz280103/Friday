package com.code_design_camp.client.friday.HeadDisplayClient.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

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
        //TODO: Change versionPre Reference to version only
        DatabaseReference updateref = db.getReference("versionPre");
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
