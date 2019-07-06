package com.friday.ar.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.ImageFormat
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.activities.FridayActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.auth.FirebaseAuth
import edu.cmu.pocketsphinx.Hypothesis
import edu.cmu.pocketsphinx.RecognitionListener
import edu.cmu.pocketsphinx.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_fullscreen_action.*
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActionActivity : FridayActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    private var ar_fragment: ArFragment? = null
    private var fridayTextRenderable: ModelRenderable? = null
    private var appManagerRenderable: ViewRenderable? = null
    private val wakeupRecognizer: SpeechRecognizer? = null
    private var speechToTextRecognizer: SpeechRecognizer? = null
    private var mArCoreSession: Session? = null
    private val speechToTextConversionListener = object : RecognitionListener {
        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {
            mic_pic!!.setImageResource(R.drawable.ic_mic_none_green_24dp)
        }

        override fun onPartialResult(hypothesis: Hypothesis?) {
            if (hypothesis != null) {
                mic_text!!.text = hypothesis.hypstr
            }
        }

        override fun onResult(hypothesis: Hypothesis) {
            mic_pic!!.setImageResource(R.drawable.ic_mic_none_green_24dp)
        }

        override fun onError(e: Exception) {
            wakeupRecognizer!!.stop()
        }

        override fun onTimeout() {
            wakeupRecognizer!!.stop()
        }
    }
    private val uiStyleUpdateListener = Scene.OnUpdateListener {
        try {
            //Acquire an image from the arcore fragment
            val img = ar_fragment!!.arSceneView.arFrame!!.acquireCameraImage()

            //create a buffer array out of it
            val bytes = ByteArray(img.planes[0].buffer.capacity())
            img.planes[0].buffer.get(bytes, 0, img.planes[0].buffer.capacity())
            val baOutputStream = ByteArrayOutputStream()

            //convert YUV-format image to JPEG(only the JPEG-format can be decoded from a byte-array
            val yuvImage = YuvImage(bytes, ImageFormat.NV21, img.width, img.height, null)
            yuvImage.compressToJpeg(Rect(0, 0, img.width, img.height), 50, baOutputStream)

            //release acquired image
            img.close()
            val byteForBitmap = baOutputStream.toByteArray()

            //create bitmap and let the palette analyze it
            val bitmap = BitmapFactory.decodeByteArray(byteForBitmap, 0, byteForBitmap.size)
            val palette = Palette.from(bitmap)
            palette.generate { generatedPalette ->
                if (isColorBright(generatedPalette!!.getDominantColor(Color.WHITE))) {
                    timetext!!.setTextColor(Color.BLACK)
                    signedin_user_text!!.setTextColor(Color.BLACK)
                } else {
                    timetext!!.setTextColor(Color.WHITE)
                    signedin_user_text!!.setTextColor(Color.WHITE)
                }
            }
        } catch (e: NotYetAvailableException) {
            Log.w("ArFragment", "Fragment not yet available")
        }

        if (mArCoreSession!!.allAnchors != null && mArCoreSession!!.getAllTrackables(Plane::class.java).isNotEmpty() && appManagerRenderable == null) {
            ViewRenderable.builder()
                    .setView(this@FullscreenActionActivity, R.layout.ar_app_manager)
                    .build()
                    .thenAccept { viewRenderable ->
                        appManagerRenderable = viewRenderable
                        for (plane in mArCoreSession!!.getAllTrackables(Plane::class.java)) {
                            if (plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING || plane.type == Plane.Type.VERTICAL) {
                                val appAnchor = mArCoreSession!!.createAnchor(
                                        ar_fragment!!.arSceneView.arFrame!!.camera.pose
                                                .compose(Pose.makeTranslation(0f, 0f, -1f))
                                                .extractTranslation())
                                val app_anchor_node = AnchorNode(appAnchor)
                                app_anchor_node.isSmoothed = true
                                app_anchor_node.setParent(ar_fragment!!.arSceneView.scene)
                                app_anchor_node.renderable = appManagerRenderable
                                mArCoreSession!!.allAnchors.add(appAnchor)
                            }
                        }
                    }
                    .exceptionally { throwable ->
                        Log.e(LOGTAG, throwable.localizedMessage, throwable)
                        null
                    }
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_action)
        //Has to be rotated because when used in AR-glasses, phone is rotated
        window.decorView.rotationY = 180f
        firebaseAuth = FirebaseAuth.getInstance()
        ar_fragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment
        val returnOnErrorIntent = Intent()
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        try {
            val apk = ArCoreApk.getInstance()
            if (apk.requestInstall(this, true) != ArCoreApk.InstallStatus.INSTALLED) {

            }
            mArCoreSession = Session(this)
            val config = mArCoreSession!!.config
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            config.focusMode = Config.FocusMode.AUTO
            mArCoreSession!!.configure(config)
            ar_fragment!!.arSceneView.setupSession(mArCoreSession)

        } catch (e: Exception) {
            Log.d(LOGTAG, "Exception ocurred")
            if (e is UnavailableArcoreNotInstalledException) {
                returnOnErrorIntent.putExtra("errtpe", "TYPE_NOT_INSTALLED")
            } else if (e is UnavailableApkTooOldException) {
                returnOnErrorIntent.putExtra("errtype", "TYPE_OLD_APK")
            } else if (e is UnavailableSdkTooOldException) {
                returnOnErrorIntent.putExtra("errtype", "TYPE_OLD_SDK_TOOL")
            } else if (e is UnavailableDeviceNotCompatibleException) {
                returnOnErrorIntent.putExtra("errtype", "TYPE_DEVICE_INCOMPATIBLE")
            }
            setResult(RESULT_CANCELED, returnOnErrorIntent)
            finishActivity(MainActivity.FULLSCREEN_REQUEST_CODE)
            return
        }

        //Following code is an example
        ModelRenderable.builder()
                .setSource(this, R.raw.project_friday_text)
                .build()
                .thenAccept { renderable -> fridayTextRenderable = renderable }
                .exceptionally { throwable ->
                    val toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }
        ar_fragment!!.planeDiscoveryController.hide()
        ar_fragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (fridayTextRenderable == null) {
                return@setOnTapArPlaneListener
            }

            // Create the Anchor.
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(ar_fragment!!.arSceneView.scene)

            // Create the transformable andy and add it to the anchor.
            val andy = TransformableNode(ar_fragment!!.transformationSystem)
            andy.setParent(anchorNode)
            andy.renderable = fridayTextRenderable
            andy.select()
        }
        ar_fragment!!.arSceneView.scene.addOnUpdateListener(uiStyleUpdateListener)
        val c = Calendar.getInstance()
        timetext!!.text = String.format("%d:%d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
        signedin_user_text!!.text = if (firebaseAuth!!.currentUser != null) firebaseAuth!!.currentUser!!.email else "Nicht angemeldet"
    }

    override fun onStart() {
        super.onStart()
        speechToTextRecognizer = (application as FridayApplication).speechToTextRecognizer
        mic_pic!!.setImageResource(R.drawable.ic_mic_off_black_24dp)
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this@FullscreenActionActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@FullscreenActionActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@FullscreenActionActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    override fun onBackPressed() {
        val confirmBack = MaterialAlertDialogBuilder(this)
                .setTitle(R.string.leave_action_activity)
                .setPositiveButton(R.string.action_leave) { dialog, dinterface -> finish() }
                .setNegativeButton(android.R.string.no, null)
                .create()
        confirmBack.show()
    }

    override fun onPause() {
        super.onPause()
        wakeupRecognizer!!.cancel()
        speechToTextRecognizer!!.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mArCoreSession!!.close()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults[0] == -1) {
            Toast.makeText(this@FullscreenActionActivity, "Declined permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isColorBright(color: Int): Boolean {
        if (android.R.color.transparent == color) {
            return true
        }
        //Calculates if the main visible contrast is black/white for the human eye
        val rgb = intArrayOf(Color.red(color), Color.green(color), Color.blue(color))
        val brightness = Math.sqrt(rgb[0].toDouble() * rgb[0].toDouble() * .241 + rgb[1].toDouble() * rgb[1].toDouble() * .691 + rgb[2].toDouble() * rgb[2].toDouble() * .068).toInt()
        return brightness >= 200
    }

    companion object {
        private const val LOGTAG = "ARActivity"
    }
}