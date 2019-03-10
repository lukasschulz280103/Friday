package com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Date;

public class ChangelogDialogFragment extends DialogFragment {
    private static final String LOGTAG = "ChangeLogDialog";
    private LinearLayout container;
    private TextView title;
    private TextView timestamp;
    private TextView body;
    private TextView errortext;
    private ProgressBar loadingbar;
    private MaterialButton dismiss;

    private String update_title;
    private String update_body;
    private Date update_timestamp;
    private FirebaseFirestore changelog = FirebaseFirestore.getInstance();
    private CollectionReference changelog_collection;
    private EventListener<DocumentSnapshot> onChangelogDocumentLoaded = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
            if (e == null && documentSnapshot.exists()) {
                update_title = (String) documentSnapshot.get("title");
                update_body = (String) documentSnapshot.get("body");
                update_timestamp = ((Timestamp) documentSnapshot.get("release_date")).toDate();
                Log.d(LOGTAG,Boolean.valueOf(documentSnapshot.get("release_date")==null).toString());
                container.setVisibility(View.VISIBLE);
                loadingbar.setVisibility(View.GONE);
                setUpdate_title(update_title);
                body.setText(HtmlCompat.fromHtml(update_body, HtmlCompat.FROM_HTML_MODE_COMPACT));
                setUpdate_timestamp(update_timestamp);
            } else if (e != null) {
                errortext.setVisibility(View.VISIBLE);
                errortext.setText(String.format("Couldn't load info. Caused by: %s", e.getLocalizedMessage()));
                setCancelable(true);
                loadingbar.setVisibility(View.GONE);
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
            } else {
                dismiss();
                Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
            }
            dismiss.setEnabled(true);
        }
    };

    public ChangelogDialogFragment() {
        // Required empty public constructor
    }

    public void setUpdate_title(String update_title) {
        this.update_title = update_title;
        title.setText(update_title);
    }

    public void setUpdate_timestamp(Date update_timestamp) {
        this.update_timestamp = update_timestamp;
        timestamp.setText(update_timestamp.toString());
    }

    public void setUpdate_body(String update_body) {
        this.update_body = update_body;
        body.setText(update_body);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        try {
            super.onCreate(savedInstanceState);
            PackageInfo pkgInf = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            Log.d("ChangeLogDialog", "versionName is " + pkgInf.versionName);
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
            changelog.setFirestoreSettings(settings);
            changelog_collection = changelog.collection("changelogs");
            DocumentReference changelog_doc = changelog_collection.document(pkgInf.versionName);
            changelog_doc.addSnapshotListener(onChangelogDocumentLoaded);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View dialogview = inflater.inflate(R.layout.fragment_changelog_dialog, container, false);
        title = dialogview.findViewById(R.id.changelog_dialog_title);
        timestamp = dialogview.findViewById(R.id.changelog_dialog_release_date);
        body = dialogview.findViewById(R.id.changelog_dialog_body);
        errortext = dialogview.findViewById(R.id.chld_error);
        loadingbar = dialogview.findViewById(R.id.changelog_dialog_progress);
        this.container = dialogview.findViewById(R.id.chld_infocontainer);
        dismiss = dialogview.findViewById(R.id.chld_dismissbtn);
        dismiss.setOnClickListener(view -> dismiss());
        dismiss.setEnabled(false);
        return dialogview;
    }

}
