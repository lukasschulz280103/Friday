package com.code_design_camp.client.friday.HeadDisplayClient.ui;

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

import com.code_design_camp.client.friday.HeadDisplayClient.FridayApplication;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActionActivity extends FridayActivity {
    private FirebaseAuth auth;
    private ConstraintLayout mic_indicator;
    private TextView time, user_email, mic_text;
    private ImageView mic_img;
    private ArFragment arFragment;
    private Session mArCoreSession;
    private ModelRenderable andyRenderable;
    private ViewRenderable settingsRenderable;
    private SpeechRecognizer recognizer, speechtoTextRecognizer;
    private RecognitionListener stt = new RecognitionListener() {
        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {
            mic_img.setImageResource(R.drawable.ic_mic_none_green_24dp);
        }

        @Override
        public void onPartialResult(Hypothesis hypothesis) {
            Log.d("STTINPUT", "converting text to speech");
            if (hypothesis != null) {
                mic_text.setText(hypothesis.getHypstr());
            }
        }

        @Override
        public void onResult(Hypothesis hypothesis) {
            mic_img.setImageResource(R.drawable.ic_mic_none_green_24dp);
        }

        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onTimeout() {

        }
    };
    private Scene.OnUpdateListener UiStyleUpdateListener = new Scene.OnUpdateListener() {
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
                Palette.Builder palette = new Palette.Builder(bitmap);
                palette.generate(palette1 -> {
                    //decide wether the font-color should be black or white
                    if (isColorBright(palette1.getDominantColor(Color.WHITE))) {
                        time.setTextColor(Color.BLACK);
                        user_email.setTextColor(Color.BLACK);
                    } else {
                        time.setTextColor(Color.WHITE);
                        user_email.setTextColor(Color.WHITE);
                    }
                });
            } catch (NotYetAvailableException e) {
                Log.w("ArFragment", "Fragment not yet available");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_action);
        init();
        ActionBar actionBar = getSupportActionBar();
        Intent returnOnErrorIntent = new Intent();
        Bundle errorInfoBundle = returnOnErrorIntent.getExtras();
        assert errorInfoBundle != null;
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
        Collection<Anchor> mAnchors = mArCoreSession.getAllAnchors();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //Following code is an example
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
        arFragment.getArSceneView().getScene().addOnUpdateListener(UiStyleUpdateListener);
        Calendar c = Calendar.getInstance();
        time.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
        user_email.setText(auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "Nicht angemeldet");
    }

    @Override
    protected void onStart() {
        super.onStart();
        speechtoTextRecognizer = ((FridayApplication) getApplication()).speechtotextrecognizer;
        mic_img.setImageResource(R.drawable.ic_mic_off_black_24dp);
        @SuppressLint("StaticFieldLeak")
        AsyncTask loadRecognizers = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Assets assets = new Assets(FullscreenActionActivity.this);
                    File assetsDir = assets.syncAssets();
                    recognizer = SpeechRecognizerSetup.defaultSetup()
                            .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                            .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                            .getRecognizer();
                    recognizer.addListener(new RecognitionListener() {
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
                                mic_text.setText("...");
                                mic_img.setImageResource(R.drawable.ic_mic_black_24dp);
                                mic_indicator.animate()
                                        .scaleXBy(0.3f)
                                        .scaleYBy(0.3f)
                                        .yBy(1.5f)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .setDuration(200)
                                        .start();
                                recognizer.stop();
                                recognizer.cancel();
                                speechtoTextRecognizer.addListener(stt);
                                speechtoTextRecognizer.startListening("input", 2000);
                            }
                        }

                        @Override
                        public void onResult(Hypothesis hypothesis) {
                            Log.d("KEYPHRASER", "result");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("KEYPHRASER", e.getLocalizedMessage(), e);
                            Toast.makeText(FullscreenActionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onTimeout() {

                        }
                    });
                    recognizer.addKeywordSearch("wakeup", new File(assetsDir, "phrases/wakeup.gram"));
                    recognizer.startListening("wakeup");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mic_img.setImageResource(R.drawable.ic_mic_none_green_24dp);
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
        AlertDialog confirmBack = new AlertDialog.Builder(this)
                .setTitle(R.string.leave_action_activity)
                .setPositiveButton(R.string.action_leave, (dialog, dinterface) -> finish())
                .setNegativeButton(android.R.string.no, null)
                .create();
        confirmBack.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recognizer.stop();
        recognizer.cancel();
        speechtoTextRecognizer.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == -1) {
                Toast.makeText(FullscreenActionActivity.this, "Declined permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);
        time = findViewById(R.id.timetext);
        user_email = findViewById(R.id.signedin_user_text);
        auth = FirebaseAuth.getInstance();
        mic_indicator = findViewById(R.id.mic_indicator);
        mic_text = findViewById(R.id.mic_text);
        mic_img = findViewById(R.id.mic_pic);
    }

    private boolean isColorBright(int color) {
        if (android.R.color.transparent == color) {
            return true;
        }
        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1] * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
        return brightness >= 200;
    }
}
