package com.friday.ar.util;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateUtil {
    private OnStateChangedListener listener;

    public UpdateUtil() {
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
