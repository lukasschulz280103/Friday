package com.friday.ar.ui.store;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.activities.FridayActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.List;

public class StoreDetailActivity extends FridayActivity {
    private static final String LOGTAG = "StoreDetailActivity";
    final long ONE_MB = 1024 * 1024;
    final long FIVE_MB = 5242880;
    OnCompleteListener manifestListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isCanceled()) {

            } else {

            }
        }
    };
    private FirebaseStorage appStorage = FirebaseStorage.getInstance();
    private StorageReference appStorageRef = appStorage.getReference("store/widgets/001");
    private FirebaseFirestore appMetaData = FirebaseFirestore.getInstance();
    private CollectionReference reviewRef = appMetaData.collection("store/review/001");
    private Task reviewRefTask = reviewRef.get();
    private Task metaDoc = reviewRef.document("meta").get();
    private Task manifestDownloadTask;
    private Task appLogoTask;
    private JSONObject manifest;
    private CircularImageView thumbnail_small;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RatingBar ratingBar;
    private ChipGroup tagContainer;
    private ProgressBar loadingSpinner;
    private TextView version_head;
    private MaterialButton start_download;
    private OnSuccessListener manifestSuccessListener = new OnSuccessListener() {

        @Override
        public void onSuccess(Object o) {
            try {
                Log.d(LOGTAG, "Loaded data");
                manifest = new JSONObject(new String((byte[]) o, Charset.forName("UTF-8")));
                JSONArray tags = manifest.getJSONArray("tags");
                loadingSpinner.setVisibility(View.GONE);
                enableScroll();
                start_download.setEnabled(true);
                collapsingToolbarLayout.setTitle(manifest.getString("name"));
                version_head.setText(getString(R.string.store_detail_version, manifest.get("versionName")));
                if (manifest.getString("versionExtra").equals("beta")) {
                    start_download.setText(R.string.beta);
                    start_download.setStrokeColorResource(R.color.colorBeta);
                    start_download.setTextColor(getResources().getColor(R.color.colorBeta));
                }
                for (int i = 0; i < tags.length(); i++) {
                    Chip tag = new Chip(StoreDetailActivity.this);
                    tag.setText((String) tags.get(i));
                    tagContainer.addView(tag);
                }
            } catch (JSONException e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
            }
        }
    };
    private OnSuccessListener logoListener = new OnSuccessListener() {
        @Override
        public void onSuccess(Object o) {

        }
    };
    private OnCompleteListener logoSuccesListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isCanceled()) {

            } else {

            }
        }
    };
    private OnCompleteListener reviewListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isCanceled()) {

            } else {

            }
        }
    };
    private OnSuccessListener reviewSuccesListener = new OnSuccessListener() {
        @Override
        public void onSuccess(Object o) {
            QuerySnapshot snapshot = (QuerySnapshot) o;
            List<DocumentSnapshot> reviewlist = snapshot.getDocuments();

        }
    };
    private OnCompleteListener metaDataListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {

        }
    };
    private OnSuccessListener metaDataSuccessListener = new OnSuccessListener() {
        @Override
        public void onSuccess(Object o) {
            DocumentSnapshot doc = (DocumentSnapshot) o;
            ratingBar.setRating(((Double) doc.get("rating_stars")).intValue());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store_detail);
        Toolbar t = findViewById(R.id.store_detail_toolbar);
        collapsingToolbarLayout = findViewById(R.id.store_collapsing_bar);
        thumbnail_small = findViewById(R.id.widget_thumbnail_round);
        loadingSpinner = findViewById(R.id.widget_detail_loading_spinner);
        version_head = findViewById(R.id.store_widget_version_header);
        start_download = findViewById(R.id.start_download);
        tagContainer = findViewById(R.id.store_detail_tags_group);
        ratingBar = findViewById(R.id.widget_rating_server);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        disableScroll();
        collapsingToolbarLayout.setTitle("");
        start_download.setEnabled(false);
        manifestDownloadTask = appStorageRef.child("/manifest.json").getBytes(ONE_MB);
        reviewRefTask.addOnSuccessListener(reviewSuccesListener);
        reviewRefTask.addOnCompleteListener(reviewListener);
        //appLogoTask = appStorageRef.child("data/logo/logo.png").getBytes(FIVE_MB);
        addStorageListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_detail_app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            default: {
                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addStorageListeners() {
        manifestDownloadTask.addOnCompleteListener(manifestListener);
        manifestDownloadTask.addOnSuccessListener(manifestSuccessListener);
        metaDoc.addOnCompleteListener(metaDataListener);
        metaDoc.addOnSuccessListener(metaDataSuccessListener);
        //appLogoTask.addOnSuccessListener(logoListener);
        //appLogoTask.addOnCompleteListener(logoSuccessListener);
    }

    private void enableScroll() {
        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams)
                collapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        );
        collapsingToolbarLayout.setLayoutParams(params);
    }

    private void disableScroll() {
        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams)
                collapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(0);
        collapsingToolbarLayout.setLayoutParams(params);
    }
}
