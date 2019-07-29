package com.friday.ar.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.crashlytics.android.Crashlytics
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener
import com.friday.ar.service.AccountSyncService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.default_signin_fragment_layout.*


//TODO renew this messed up code
class DefaultSignInFragment : Fragment() {
    private var resetResultText: TextView? = null
    private var resetPasswordText: TextView? = null
    private var onAuthCompletedListenerList = ArrayList<OnAuthCompletedListener>()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var emailInput: TextInputEditText? = null
    private var emailInputWrapper: TextInputLayout? = null
    private var googleSignInButton: SignInButton? = null
    private var mSignInClient: GoogleSignInClient? = null
    private lateinit var mActivity: Activity
    private var loader: ProgressBar? = null
    private var passwordEditText: TextInputEditText? = null
    private var submitbtn: MaterialButton? = null
    private val createNewUserCompleteListener = OnCompleteListener<AuthResult> { task ->
        loader!!.visibility = View.INVISIBLE
        setInputsEnabled(true)
        if (task.isSuccessful) {

        } else {
            try {
                throw task.exception!!
            } catch (e: Exception) {
                Log.e(LOGTAG, e.localizedMessage, e)
            }

        }
    }

    private val onSignInCompletionListener = OnCompleteListener<AuthResult> { task ->
        loader!!.visibility = View.INVISIBLE
        setInputsEnabled(true)
        if (task.isSuccessful) {
            Snackbar.make(mActivity.findViewById(android.R.id.content), getString(R.string.signin_welcome, emailInput!!.text!!.toString()), Snackbar.LENGTH_SHORT).show()
            notifyAuthCompleted()
        } else {
            val errorDialogBuilder = MaterialAlertDialogBuilder(context!!)
            try {
                throw task.exception!!
            } catch (e: FirebaseAuthEmailException) {
                errorDialogBuilder.setTitle(R.string.exception_email_title)
                errorDialogBuilder.setMessage(getString(R.string.exception_email_msg, e.localizedMessage))
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                errorDialogBuilder.setTitle(R.string.exception_invalid_credentials_title)
                errorDialogBuilder.setMessage(getString(R.string.exception_invalid_credentials_msg, e.localizedMessage))
            } catch (e: FirebaseAuthInvalidUserException) {
                val code = e.errorCode
                when (code) {
                    "ERROR_USER_DISABLED" -> {
                        errorDialogBuilder.setTitle(R.string.exception_user_disabled_title)
                        errorDialogBuilder.setMessage(R.string.exception_user_disabled_msg)
                        errorDialogBuilder.setNeutralButton(R.string.more, null)
                    }
                    "ERROR_USER_NOT_FOUND" -> {
                        errorDialogBuilder.setTitle(R.string.exception_user_not_found_title)
                        errorDialogBuilder.setMessage(getString(R.string.exception_user_not_found_msg, emailInput!!.text!!.toString()))
                        errorDialogBuilder.setNeutralButton(R.string.action_createnew_account) { _, _ ->
                            val crUser = firebaseAuth.createUserWithEmailAndPassword(emailInput!!.text!!.toString(), passwordEditText!!.text!!.toString())
                            crUser.addOnCompleteListener(createNewUserCompleteListener)
                            loader!!.visibility = View.VISIBLE
                            setInputsEnabled(false)
                        }
                    }
                    else -> {
                        errorDialogBuilder.setTitle(e.errorCode)
                        errorDialogBuilder.setMessage(e.localizedMessage)
                    }
                }
            } catch (e: Exception) {
                errorDialogBuilder.setTitle(e.message)
                errorDialogBuilder.setTitle(e.localizedMessage)
                Log.e(LOGTAG, "Unknown error occured while user tried to sign in:" + e.message, e)

            }

            errorDialogBuilder.setPositiveButton(android.R.string.ok, null)
            errorDialogBuilder.create().show()
        }
    }
    private var mOnEditorActionListener = TextView.OnEditorActionListener { _, imeAction, _ ->
        if (imeAction == EditorInfo.IME_ACTION_DONE) {
            validateEmail()
            return@OnEditorActionListener true
        }
        false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.default_signin_fragment_layout, container, false)
        passwordEditText = fragmentView.findViewById(R.id.email_password)
        resetPasswordText = fragmentView.findViewById(R.id.password_forgot)
        submitbtn = fragmentView.findViewById(R.id.submit)
        loader = fragmentView.findViewById(R.id.signin_progress)
        emailInputWrapper = fragmentView.findViewById(R.id.email_input_layout)
        emailInput = fragmentView.findViewById(R.id.email_input_signin)
        googleSignInButton = fragmentView.findViewById(R.id.signin_google_button)
        submitbtn!!.setOnClickListener { validateEmail() }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.request_id_token))
                .requestEmail()
                .build()
        mSignInClient = GoogleSignIn.getClient(mActivity, gso)
        googleSignInButton!!.setOnClickListener {
            val signInIntent = mSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        resetPasswordText!!.setOnClickListener { resetPassword() }
        emailInput!!.setOnEditorActionListener(mOnEditorActionListener)
        return fragmentView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mActivity = context as AppCompatActivity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Crashlytics.setUserName(account!!.displayName)
                Crashlytics.setUserEmail(account.email)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                val apiErrorDialog = MaterialAlertDialogBuilder(context!!)
                if (e.statusCode == 10) {
                    apiErrorDialog.setTitle(R.string.simple_action_error_title)
                    apiErrorDialog.setMessage(R.string.auth_server_error_msg)
                    Crashlytics.log(Log.ERROR, "ServerError", "ApiException 10: could not sign in user: fingerprint invalid")
                    Crashlytics.logException(e)
                } else {
                    apiErrorDialog.setTitle(e.statusCode.toString() + ": " + getString(R.string.internal_error_title))
                    apiErrorDialog.setMessage(e.message)
                }
                Log.e(TAG, "Google sign in failed", e)
                apiErrorDialog.setPositiveButton(android.R.string.ok, null)
                apiErrorDialog.create().show()

            }

        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity) { task ->
                    if (task.isSuccessful) {
                        Crashlytics.setUserIdentifier(firebaseAuth.currentUser!!.uid)
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = firebaseAuth.currentUser!!
                        val jobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                        val jobInfo = JobInfo.Builder(FridayApplication.Jobs.JOB_SYNC_ACCOUNT, ComponentName(context!!, AccountSyncService::class.java))
                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                .setBackoffCriteria(60000 * 5, JobInfo.BACKOFF_POLICY_LINEAR)
                                .build()
                        jobScheduler.schedule(jobInfo)
                        Snackbar.make(mActivity.findViewById(android.R.id.content), getString(R.string.signin_welcome, user.displayName), Snackbar.LENGTH_SHORT).show()
                        notifyAuthCompleted()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        val apiErrorDialog = MaterialAlertDialogBuilder(context!!)
                        apiErrorDialog.setTitle(R.string.simple_action_error_title)
                        if (task.exception != null) apiErrorDialog.setMessage(task.exception!!.message)
                        else apiErrorDialog.setMessage(R.string.unknown_error)
                        apiErrorDialog.setPositiveButton(android.R.string.ok, null)
                        apiErrorDialog.create().show()
                    }
                }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        emailInput!!.isEnabled = enabled
        passwordEditText!!.isEnabled = enabled
        resetPasswordText!!.isEnabled = enabled
        submitbtn!!.isEnabled = enabled
    }


    //TODO: This method should not handle UI related things and should be in class com.frida.ar.util.Validator
    private fun validateEmail(): Boolean {
        val text = emailInput!!.text!!.toString()
        val pswdText = passwordEditText!!.text.toString()
        if (text.isEmpty() || !text.contains(".") || !text.contains("@")) {
            emailInputWrapper!!.error = getString(R.string.mail_invalid_error)
            return false
        } else if (pswdText.isEmpty()) {
            passwordInputWrapper.error = getString(R.string.auth_pswd_empty_error)
            return false
        } else {
            loader!!.visibility = View.VISIBLE
            setInputsEnabled(false)
            firebaseAuth.signInWithEmailAndPassword(emailInput!!.text!!.toString(), passwordEditText!!.text!!.toString()).addOnCompleteListener(onSignInCompletionListener)
            return true
        }
    }

    @SuppressLint("InflateParams")
    private fun resetPassword() {
        val askResetPassword = MaterialAlertDialogBuilder(context!!)
        askResetPassword.setTitle(R.string.auth_forgot_password)
        val dialogView = mActivity.layoutInflater.inflate(R.layout.reset_password_dialog, null, false)
        (dialogView.findViewById<View>(R.id.email) as TextView).text = emailInput!!.text!!.toString()
        resetResultText = dialogView.findViewById(R.id.reset_result_text)
        val flipper = dialogView.findViewById<ViewFlipper>(R.id.viewflipper)
        askResetPassword.setView(dialogView)
        askResetPassword.setPositiveButton(R.string.auth_send_reset_pswd_email, null)
        askResetPassword.setNegativeButton(android.R.string.cancel, null)
        val askResetPasswordDialog = askResetPassword.create()
        askResetPasswordDialog.setOnShowListener {
            val positive = askResetPasswordDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val negative = askResetPasswordDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            positive.setOnClickListener {
                askResetPasswordDialog.setCancelable(false)
                //TODO check email validity to verify that email is not empty
                val resetPassword = firebaseAuth.sendPasswordResetEmail(emailInput!!.text!!.toString())
                flipper.showNext()
                positive.isEnabled = false
                negative.visibility = View.GONE
                resetPassword.addOnCompleteListener { task ->
                    positive.isEnabled = true
                    flipper.showNext()
                    positive.setText(android.R.string.ok)
                    if (task.isSuccessful) {
                        resetResultText!!.setText(R.string.email_reset_succes)
                    } else {
                        resetResultText!!.text = getString(R.string.email_reset_error, task.exception!!.localizedMessage)
                    }
                    positive.setOnClickListener { askResetPasswordDialog.dismiss() }
                }
            }

        }
        askResetPasswordDialog.show()
    }

    fun addOnAuthCompletedListener(listener: OnAuthCompletedListener) {
        onAuthCompletedListenerList.add(listener)
    }

    fun removeOnAuthCompletedListener(listener: OnAuthCompletedListener) {
        onAuthCompletedListenerList.remove(listener)
    }

    private fun notifyAuthCompleted() {
        onAuthCompletedListenerList.forEach { listener -> listener.onAuthCompleted() }
    }

    companion object {
        private const val LOGTAG = "DeafultSigninFragment"
        private const val RC_SIGN_IN = 9001
    }

}//required empty constructor
