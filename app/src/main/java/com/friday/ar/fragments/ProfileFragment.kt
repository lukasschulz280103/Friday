package com.friday.ar.fragments

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.friday.ar.Constant
import com.friday.ar.R
import com.friday.ar.fragments.dialogFragments.AuthDialog
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener
import com.friday.ar.ui.FeedbackSenderActivity
import com.friday.ar.util.UserUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mikhaellopez.circularimageview.CircularImageView

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    companion object {
        private const val LOGTAG = "ProfileFragment"
        private const val REQUEST_CODE_SETTINGS = 200
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var mContext: Context

    private var accountImageView: CircularImageView? = null
    private var emailText: TextView? = null
    private var welcomeText: TextView? = null

    private var viewSwitcher: ViewSwitcher? = null
    private val intentManager = View.OnClickListener { view ->
        when (view.id) {
            R.id.main_layout_editor -> {
                Toast.makeText(context, R.string.feature_soon, Toast.LENGTH_SHORT).show()
            }
            R.id.main_settings -> {
                startActivityForResult(Intent("com.friday.settings"), REQUEST_CODE_SETTINGS)
            }
            R.id.main_help -> {
                Toast.makeText(context, R.string.feature_soon, Toast.LENGTH_SHORT).show()
            }
            R.id.main_feedback -> {
                startActivity(Intent(activity, FeedbackSenderActivity::class.java))
            }
        }
    }

    private val accountSynchronizedReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            setupSignInScreen()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_profile, container, false)
        viewSwitcher = fragmentView.findViewById(R.id.page_profile_account_vswitcher)
        emailText = fragmentView.findViewById(R.id.page_profile_email)
        accountImageView = fragmentView.findViewById(R.id.page_profile_image_account)
        welcomeText = fragmentView.findViewById(R.id.page_profile_header)
        val signInButton = fragmentView.findViewById<Button>(R.id.page_profile_signin_button)
        val toFeedback = fragmentView.findViewById<LinearLayout>(R.id.main_feedback)
        val toSettings = fragmentView.findViewById<LinearLayout>(R.id.main_settings)
        val toLayoutEditor = fragmentView.findViewById<LinearLayout>(R.id.main_layout_editor)
        val toHelp = fragmentView.findViewById<LinearLayout>(R.id.main_help)
        signInButton.setOnClickListener {
            signInButton.isEnabled = false
            val authenticationDialog = AuthDialog()
            authenticationDialog.show(childFragmentManager, "AuthenticationDialog")
            authenticationDialog.addOnAuthCompletedListener(object : OnAuthCompletedListener {
                override fun onAuthCompleted() {
                    setupSignInScreen()
                    authenticationDialog.dismiss()
                }
            })
            authenticationDialog.setOnDismissListener(DialogInterface.OnDismissListener {
                signInButton.isEnabled = true
            })
        }
        toLayoutEditor.setOnClickListener(intentManager)
        toSettings.setOnClickListener(intentManager)
        toHelp.setOnClickListener(intentManager)
        toFeedback.setOnClickListener(intentManager)
        setupSignInScreen()

        mContext.registerReceiver(accountSynchronizedReceiver, IntentFilter(Constant.BroadcasteceiverActions.BROADCAST_ACCOUNT_SYNCED))
        return fragmentView
    }

    override fun onDestroy() {
        super.onDestroy()
        mContext.unregisterReceiver(accountSynchronizedReceiver)
    }

    override fun onResume() {
        super.onResume()
        setupSignInScreen()
    }

    private fun setupSignInScreen() {
        if (firebaseAuth.currentUser == null) {
            viewSwitcher!!.displayedChild = 0
        } else {
            firebaseUser = firebaseAuth.currentUser
            val userUtil = UserUtil(mContext)
            viewSwitcher!!.displayedChild = 1

            if (firebaseUser!!.photoUrl != null && userUtil.avatarFile.exists()) {
                accountImageView!!.setImageURI(Uri.parse(userUtil.avatarFile.path))
            } else {
                accountImageView!!.background = activity!!.getDrawable(R.drawable.ic_twotone_account_circle_24px)
            }
            emailText!!.text = firebaseUser!!.email
            welcomeText!!.text = if (firebaseUser!!.displayName != null && firebaseUser!!.displayName != "") getString(R.string.page_profile_header_text, firebaseUser!!.displayName) else getString(R.string.greet_no_name)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(LOGTAG, "onActivityResult:[requestCode=$requestCode|resultCode=$resultCode|oldData=$data")
        if (requestCode == REQUEST_CODE_SETTINGS && resultCode == Activity.RESULT_OK) {
            activity!!.recreate()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
