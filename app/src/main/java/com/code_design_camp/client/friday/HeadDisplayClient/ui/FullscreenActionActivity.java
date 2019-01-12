package com.code_design_camp.client.friday.HeadDisplayClient.ui;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActionActivity extends FridayActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    FrameLayout mContentView;
    LinearLayout hintView;
    ArFragment arFragment;
    private boolean mVisible;
    Session mArCoreSession;
    private ModelRenderable andyRenderable;
    private ViewRenderable settingsRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_action);
        mContentView = findViewById(R.id.root_fullscreen);
        hintView = findViewById(R.id.helphint);
        ActionBar actionBar = getSupportActionBar();
        Intent returnOnErrorIntent = new Intent();
        Bundle errorInfoBundle = returnOnErrorIntent.getExtras();
        try {
            mArCoreSession = new Session(FullscreenActionActivity.this);
        } catch (UnavailableArcoreNotInstalledException e) {
            errorInfoBundle.putString("errtype", "TYPE_NOT_INSTALLED");
        } catch (UnavailableApkTooOldException e) {
            errorInfoBundle.putString("errtype", "TYPE_OLD_APK");
        } catch (UnavailableSdkTooOldException e) {
            errorInfoBundle.putString("errtype", "TYPE_OLD_SDK_TOOL");
        } catch (UnavailableDeviceNotCompatibleException e) {
            errorInfoBundle.putString("errtype", "TYPE_DEVICE_INCOMPATIBLE");
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        hintView.setOnClickListener(view -> {
            TextView hint_content = view.findViewById(R.id.hint_content);
            ImageView hint_icon = view.findViewById(R.id.hint_icon);
            if (hint_content.isShown()) {
                hint_content.setVisibility(View.GONE);
                animateImageResourceChange(hint_icon, R.drawable.ic_live_help_black_24dp);
            } else {
                hint_content.setVisibility(View.VISIBLE);
                animateImageResourceChange(hint_icon, R.drawable.ic_keyboard_arrow_down_black_24dp);
            }
        });
        mVisible = true;
        // Set up the user interaction to manually show or hide the system UI
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);
        //Following code is for the example
        ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }
                    CompletableFuture<ViewRenderable> settingsStage = ViewRenderable.builder().setView(FullscreenActionActivity.this, R.layout.ar_settings_layout).build();
                    settingsStage.thenAccept(viewRenderable -> {
                        try {
                            settingsRenderable = settingsStage.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Node solarControls = new Node();
                        Anchor anchor = hitResult.createAnchor();
                        AnchorNode parent = new AnchorNode(anchor);
                        parent.setParent(arFragment.getArSceneView().getScene());
                        solarControls.setParent(parent);
                        solarControls.setRenderable(settingsRenderable);
                        solarControls.setLocalPosition(new Vector3(0.5f, 0.5f, 0.0f));
                    });
                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(andyRenderable);
                    andy.select();
                });
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(FullscreenActionActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(FullscreenActionActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FullscreenActionActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog confirmBack = new AlertDialog.Builder(this)
                .setTitle(R.string.leave_action_activity)
                .setPositiveButton(R.string.action_leave, (dialog, dinterface) -> finish())
                .setNegativeButton(android.R.string.no, null)
                .create();
        confirmBack.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == -1) {
                Toast.makeText(FullscreenActionActivity.this, "Declined permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void animateImageResourceChange(ImageView view, @DrawableRes int res) {
        view.animate().alpha(0).setDuration(50).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setImageDrawable(getDrawable(res));
                view.animate().alpha(1).setDuration(50).start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }
}
