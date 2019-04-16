package com.friday.ar.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.activities.FridayActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActionActivity extends FridayActivity {
    public static final String LOGTAG = "ARActivity";
    private FirebaseAuth firebaseAuth;
    private ConstraintLayout microphoneIndicator;
    private TextView timeLabel, userEmail, microphoneText;
    private ImageView microphoneImage;
    private ArFragment arFragment;
    private ModelRenderable fridayTextRenderable;
    private ViewRenderable appManagerRenderable;
    private SpeechRecognizer wakeupRecognizer, speechToTextRecognizer;
    private Session mArCoreSession;
    private RecognitionListener speechToTextConversionListener = new RecognitionListener() {
        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onEndOfSpeech() {
            microphoneImage.setImageResource(R.drawable.ic_mic_none_green_24dp);
        }

        @Override
        public void onPartialResult(Hypothesis hypothesis) {
            if (hypothesis != null) {
                microphoneText.setText(hypothesis.getHypstr());
            }
        }

        @Override
        public void onResult(Hypothesis hypothesis) {
            microphoneImage.setImageResource(R.drawable.ic_mic_none_green_24dp);
        }

        @Override
        public void onError(Exception e) {
            wakeupRecognizer.stop();
        }

        @Override
        public void onTimeout() {
            wakeupRecognizer.stop();
        }
    };
    private Scene.OnUpdateListener uiStyleUpdateListener = new Scene.OnUpdateListener() {
        @Override
        public void onUpdate(FrameTime frameTime) {
            try {
                //Acquire an image from the arcore fragment
                Image img = arFragment.getArSceneView().getArFrame().acquireCameraImage();

                //create a buffer array out of it
                byte[] bytes = new byte[img.getPlanes()[0].getBuffer().capacity()];
                img.getPlanes()[0].getBuffer().get(bytes, 0, img.getPlanes()[0].getBuffer().capacity());
                ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();

                //convert YUV-format image to JPEG(only the JPEG-format can be decoded from a byte-array
                YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, img.getWidth(), img.getHeight(), null);
                yuvImage.compressToJpeg(new Rect(0, 0, img.getWidth(), img.getHeight()), 50, baOutputStream);

                //release acquired image
                img.close();
                byte[] byteForBitmap = baOutputStream.toByteArray();

                //create bitmap and let the palette analyze it
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteForBitmap, 0, byteForBitmap.length);
                Palette.Builder palette = Palette.from(bitmap);
                palette.generate(generatedPalette -> {
                    if (isColorBright(generatedPalette.getDominantColor(Color.WHITE))) {
                        timeLabel.setTextColor(Color.BLACK);
                        userEmail.setTextColor(Color.BLACK);
                    } else {
                        timeLabel.setTextColor(Color.WHITE);
                        userEmail.setTextColor(Color.WHITE);
                    }
                });
            } catch (NotYetAvailableException e) {
                Log.w("ArFragment", "Fragment not yet available");
            }
            if (mArCoreSession.getAllAnchors() != null && mArCoreSession.getAllTrackables(Plane.class).size() != 0 && appManagerRenderable == null) {
                ViewRenderable.builder()
                        .setView(FullscreenActionActivity.this, R.layout.ar_app_manager)
                        .build()
                        .thenAccept(viewRenderable -> {
                            appManagerRenderable = viewRenderable;
                            for (Plane plane : mArCoreSession.getAllTrackables(Plane.class)) {
                                if (plane.getType() == Plane.Type.HORIZONTAL_UPWARD_FACING
                                        || plane.getType() == Plane.Type.VERTICAL) {
                                    Anchor appAnchor = mArCoreSession.createAnchor(
                                            arFragment.getArSceneView().getArFrame().getCamera().getPose()
                                                    .compose(Pose.makeTranslation(0, 0, -1f))
                                                    .extractTranslation());
                                    AnchorNode app_anchor_node = new AnchorNode(appAnchor);
                                    app_anchor_node.setSmoothed(true);
                                    app_anchor_node.setParent(arFragment.getArSceneView().getScene());
                                    app_anchor_node.setRenderable(appManagerRenderable);
                                    mArCoreSession.getAllAnchors().add(appAnchor);
                                }
                            }
                        })
                        .exceptionally(throwable -> {
                            Log.e(LOGTAG, throwable.getLocalizedMessage(), throwable);
                            return null;
                        });
            }
        }
    };

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_action);
        //Has to be rotated because when used in AR-glasses, phone is rotated
        getWindow().getDecorView().setRotationY(180f);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);
        timeLabel = findViewById(R.id.timetext);
        userEmail = findViewById(R.id.signedin_user_text);
        firebaseAuth = FirebaseAuth.getInstance();
        microphoneIndicator = findViewById(R.id.mic_indicator);
        microphoneText = findViewById(R.id.mic_text);
        microphoneImage = findViewById(R.id.mic_pic);
        Intent returnOnErrorIntent = new Intent();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        try {
            ArCoreApk apk = ArCoreApk.getInstance();
            if (apk.requestInstall(this, true) != ArCoreApk.InstallStatus.INSTALLED) {

            }
            mArCoreSession = new Session(this);
            Config config = mArCoreSession.getConfig();
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            config.setFocusMode(Config.FocusMode.AUTO);
            mArCoreSession.configure(config);
            arFragment.getArSceneView().setupSession(mArCoreSession);

        } catch (Exception e) {
            Log.d(LOGTAG, "Exception ocurred");
            if (e instanceof UnavailableArcoreNotInstalledException) {
                returnOnErrorIntent.putExtra("errtpe", "TYPE_NOT_INSTALLED");
            } else if (e instanceof UnavailableApkTooOldException) {
                returnOnErrorIntent.putExtra("errtype", "TYPE_OLD_APK");
            } else if (e instanceof UnavailableSdkTooOldException) {
                returnOnErrorIntent.putExtra("errtype", "TYPE_OLD_SDK_TOOL");
            } else if (e instanceof UnavailableDeviceNotCompatibleException) {
                returnOnErrorIntent.putExtra("errtype", "TYPE_DEVICE_INCOMPATIBLE");
            }
            setResult(RESULT_CANCELED, returnOnErrorIntent);
            finishActivity(MainActivity.FULLSCREEN_REQUEST_CODE);
            return;
        }

        //Following code is an example
        ModelRenderable.builder()
                .setSource(this, R.raw.project_friday_text)
                .build()
                .thenAccept(renderable -> fridayTextRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (fridayTextRenderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(fridayTextRenderable);
                    andy.select();
                });
        arFragment.getArSceneView().getScene().addOnUpdateListener(uiStyleUpdateListener);
        Calendar c = Calendar.getInstance();
        timeLabel.setText(String.format("%d:%d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
        userEmail.setText(firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getEmail() : "Nicht angemeldet");
    }

    @Override
    protected void onStart() {
        super.onStart();
        speechToTextRecognizer = ((FridayApplication) getApplication()).speechToTextRecognizer;
        microphoneImage.setImageResource(R.drawable.ic_mic_off_black_24dp);
        @SuppressLint("StaticFieldLeak")
        AsyncTask loadRecognizers = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                wakeupRecognizer = ((FridayApplication) getApplication()).getSpeechToTextRecognizer();
                wakeupRecognizer.addListener(new RecognitionListener() {
                    @Override
                    public void onBeginningOfSpeech() {

                    }

                    @Override
                    public void onEndOfSpeech() {

                    }

                    @Override
                    public void onPartialResult(Hypothesis hypothesis) {
                        Log.d("KEYPHRASER", "partial result");
                        if (hypothesis != null) {
                            microphoneText.setText("...");
                            microphoneImage.setImageResource(R.drawable.ic_mic_black_24dp);
                            microphoneIndicator.animate()
                                    .scaleXBy(0.3f)
                                    .scaleYBy(0.3f)
                                    .yBy(1f)
                                    .setInterpolator(new AccelerateDecelerateInterpolator())
                                    .setDuration(200)
                                    .start();
                            wakeupRecognizer.stop();
                            wakeupRecognizer.cancel();
                            speechToTextRecognizer.addListener(speechToTextConversionListener);
                            speechToTextRecognizer.startListening("input", 2000);
                        }
                    }

                    @Override
                    public void onResult(Hypothesis hypothesis) {
                        Log.d("KEYPHRASER", "result");
                        speechToTextRecognizer.stop();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("KEYPHRASER", e.getLocalizedMessage(), e);
                        Toast.makeText(FullscreenActionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTimeout() {
                        speechToTextRecognizer.stop();
                    }
                });
                wakeupRecognizer.addKeywordSearch("wakeup", new File(((FridayApplication) getApplication()).getAssetsDir(), "phrases/wakeup.gram"));
                wakeupRecognizer.startListening("wakeup");
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                microphoneImage.setImageResource(R.drawable.ic_mic_none_green_24dp);
            }
        };
        loadRecognizers.execute();
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
        AlertDialog confirmBack = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.leave_action_activity)
                .setPositiveButton(R.string.action_leave, (dialog, dinterface) -> finish())
                .setNegativeButton(android.R.string.no, null)
                .create();
        confirmBack.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeupRecognizer.cancel();
        speechToTextRecognizer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mArCoreSession.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == -1) {
            Toast.makeText(FullscreenActionActivity.this, "Declined permission", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isColorBright(int color) {
        if (android.R.color.transparent == color) {
            return true;
        }
        //Calculates if the main visible contrast is black/white for the human eye
        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1] * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
        return brightness >= 200;
    }
}
