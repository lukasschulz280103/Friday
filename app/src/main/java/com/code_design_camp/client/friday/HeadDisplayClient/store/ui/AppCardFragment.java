package com.code_design_camp.client.friday.HeadDisplayClient.store.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.code_design_camp.client.friday.HeadDisplayClient.FridayApplication;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ErrorDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.net.ConnectionFragment;
import com.code_design_camp.client.friday.HeadDisplayClient.store.data.WidgetInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppCardFragment extends Fragment {
    public static final String LOGTAG = FridayApplication.LOGTAG_STORE;
    ImageView thumbnail;
    private ErrorDialog errorDialog;
    private CollectionReference mAppRef;
    private TextView title;
    private TextView author;
    private TextView price;
    private ProgressBar loader;
    private OnCompleteListener completeListener = task -> {
        loader.setVisibility(View.GONE);
        if (task.isSuccessful()) {
            DocumentReference manifestReference = mAppRef.document("manifest");
            DocumentReference metadataReference = mAppRef.document("metadata");
            manifestReference.addSnapshotListener((snapshot, e) -> {
                if (e == null) {
                    title.setText(snapshot.getString("appTitle"));
                } else {
                    showErrorDialog(e);
                    ((ConnectionFragment) getParentFragment().getParentFragment()).onError(e);
                }
            });
            metadataReference.addSnapshotListener((snapshot, e) -> {
                if (e == null) {
                    WidgetInfo.ExtraInfo extraInf = new WidgetInfo.ExtraInfo((Map) snapshot.get("extra"));
                    if (snapshot.getDouble("price") == 0.0) {
                        price.setText("Free");
                    } else {
                        price.setText(String.valueOf(snapshot.get("price")));
                    }
                } else {
                    showErrorDialog(e);
                    ((ConnectionFragment) getParentFragment().getParentFragment()).onError(e);
                }
            });
        } else if (task.isCanceled()) {
            Exception e = task.getException();
            showErrorDialog(e);
            ((ConnectionFragment) getParentFragment().getParentFragment()).onError(e);
        }
    };


    public AppCardFragment(CollectionReference appRef) {
        this.mAppRef = appRef;
        //CollectionReference is already apps reference
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.store_app_card, container, false);
        thumbnail = v.findViewById(R.id.thumb);
        title = v.findViewById(R.id.card_title);
        author = v.findViewById(R.id.author);
        price = v.findViewById(R.id.price);
        loader = v.findViewById(R.id.loader);
        Task loadedData = mAppRef.get();
        loadedData.addOnCompleteListener(completeListener);
        return v;
    }

    private void showErrorDialog(Exception e) {
        if (errorDialog == null || !errorDialog.isShown()) {
            errorDialog = new ErrorDialog(getActivity(), R.drawable.ic_warning_black_24dp, e);
            errorDialog.show();
        }
    }
}
