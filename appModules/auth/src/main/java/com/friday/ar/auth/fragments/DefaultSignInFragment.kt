package com.friday.ar.auth.fragments

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
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.crashlytics.android.Crashlytics
import com.friday.ar.auth.R
import com.friday.ar.auth.interfaces.OnAuthCompletedListener
import com.friday.ar.core.util.validation.Validator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import extensioneer.notNull
import kotlinx.android.synthetic.main.default_signin_fragment_layout.*
import kotlinx.android.synthetic.main.default_signin_fragment_layout.view.*
import kotlinx.android.synthetic.main.reset_password_dialog.view.*
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel


class DefaultSignInFragment : Fragment() {
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private val viewModel: SignInFragmentViewModel by viewModel { get() }

    private lateinit var fragmentView: View
    private lateinit var mContext: Context
    private var onAuthCompletedListenerList = ArrayList<OnAuthCompletedListener>()

    fun addOnAuthCompletedListener(listener: OnAuthCompletedListener) = onAuthCompletedListenerList.add(listener)
    @Suppress("unused")
    fun removeOnAuthCompletedListener(listener: OnAuthCompletedListener) = onAuthCompletedListenerList.remove(listener)

    private var mOnEditorActionListener = TextView.OnEditorActionListener { _, imeAction, _ ->
        if (imeAction == EditorInfo.IME_ACTION_DONE) {
            submitForm()
            return@OnEditorActionListener true
        }
        false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.default_signin_fragment_layout, container, false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.request_id_token))
                .requestEmail()
                .build()
        val mSignInClient = GoogleSignIn.getClient(activity!!, gso)
        mSignInClient.signOut()
        fragmentView.submit.setOnClickListener { submitForm() }
        fragmentView.signin_google_button.setOnClickListener {
            val signInIntent = mSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        fragmentView.password_forgot.setOnClickListener { resetPassword() }
        fragmentView.email_password.setOnEditorActionListener(mOnEditorActionListener)
        return fragmentView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context

        //Wait for the viewModel to attach and then set all the listeners
        viewModel.onAuthCompletedListenerList.addAll(onAuthCompletedListenerList)
        viewModel.inputsUsabilityState.observe(this, Observer { shouldEnableInputs ->
            setInputsEnabled(shouldEnableInputs)
        })
        viewModel.newUserCretionTask.observe(this, Observer { task ->
            if (task.isSuccessful) {
                MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.auth_user_creation_success)
                        .setMessage(R.string.auth_user_creation_success_hint)
                        .setIcon(R.drawable.ic_twotone_account_circle_24px)
                        .setPositiveButton(R.string.lets_go, null)
                        .create().show()
            } else {
                task.exception.notNull {
                    MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.unknown_error)
                            .setMessage(R.string.auth_could_not_create_new_user)
                            .setIcon(R.drawable.ic_twotone_error_outline_24px)
                            .setPositiveButton(R.string.lets_go, null)
                            .create().show()
                }
            }
        })

        viewModel.onEmailSigninCompleted.observe(this, Observer { task ->
            if (task.isSuccessful) {
                Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.signin_welcome, fragmentView.email_input_signin.text.toString()), Snackbar.LENGTH_SHORT).show()
            } else {
                val errorDialogBuilder = MaterialAlertDialogBuilder(mContext)
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthEmailException) {
                    errorDialogBuilder.setTitle(R.string.exception_email_title)
                    errorDialogBuilder.setMessage(getString(R.string.exception_email_msg, e.localizedMessage))

                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    errorDialogBuilder.setTitle(R.string.exception_invalid_credentials_title)
                    errorDialogBuilder.setMessage(getString(R.string.exception_invalid_credentials_msg, e.localizedMessage))
                } catch (e: FirebaseAuthInvalidUserException) {
                    when (e.errorCode) {
                        "ERROR_USER_DISABLED" -> {
                            errorDialogBuilder.setTitle(R.string.exception_user_disabled_title)
                            errorDialogBuilder.setMessage(R.string.exception_user_disabled_msg)
                            errorDialogBuilder.setNeutralButton(R.string.more, null)
                        }
                        "ERROR_USER_NOT_FOUND" -> {
                            errorDialogBuilder.setTitle(R.string.exception_user_not_found_title)
                            errorDialogBuilder.setMessage(getString(R.string.exception_user_not_found_msg, fragmentView.email_input_signin.text.toString()))
                            errorDialogBuilder.setNeutralButton(R.string.action_createnew_account) { _, _ ->
                                viewModel.createUser(fragmentView.email_input_signin.text.toString(), fragmentView.email_password.text.toString())
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
                }
                errorDialogBuilder.setPositiveButton(android.R.string.ok, null)
                errorDialogBuilder.create().show()
            }
        })

        viewModel.onGoogleSignInCompleted.observe(this, Observer { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser!!
                Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.signin_welcome, user.displayName), Snackbar.LENGTH_SHORT).show()
            } else {
                val apiErrorDialog = MaterialAlertDialogBuilder(mContext)
                apiErrorDialog.setTitle(R.string.simple_action_error_title)
                if (task.exception != null) apiErrorDialog.setMessage(task.exception!!.message)
                else apiErrorDialog.setMessage(R.string.unknown_error)
                apiErrorDialog.setPositiveButton(android.R.string.ok, null)
                apiErrorDialog.create().show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                viewModel.firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                val apiErrorDialog = MaterialAlertDialogBuilder(mContext)
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

    private fun submitForm() {
        if (!Validator.validateEmail(email_input_signin.text.toString())) {
            fragmentView.email_input_layout.error = getString(R.string.mail_invalid_error)
        } else if (email_password.text.toString().isEmpty()) {
            fragmentView.passwordInputWrapper.error = getString(R.string.auth_pswd_empty_error)
        } else {
            viewModel.signinUserWithEmailAndPassword(fragmentView.email_input_signin.text.toString(), fragmentView.email_password.text.toString())
        }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        fragmentView.email_input_signin.isEnabled = enabled
        fragmentView.email_password.isEnabled = enabled
        fragmentView.password_forgot.isEnabled = enabled
        fragmentView.submit.isEnabled = enabled
        if (enabled) fragmentView.signin_progress.visibility = View.INVISIBLE
        else fragmentView.signin_progress.visibility = View.VISIBLE
    }

    private fun resetPassword() {
        val askResetPassword = MaterialAlertDialogBuilder(mContext)
        askResetPassword.setTitle(R.string.auth_forgot_password)
        val dialogView = activity!!.layoutInflater.inflate(R.layout.reset_password_dialog, null, false)
        (dialogView.findViewById<View>(R.id.email) as TextView).text = fragmentView.email_input_signin.text.toString()
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
                if (Validator.validateEmail(email_input_signin.text.toString())) {
                    viewModel.onPasswordResetCompleted.observe(this, Observer { task ->
                        positive.isEnabled = true
                        flipper.showNext()
                        positive.setText(android.R.string.ok)
                        if (task.isSuccessful) {
                            dialogView.reset_result_text.setText(R.string.email_reset_succes)
                        } else {
                            dialogView.reset_result_text.text = getString(R.string.email_reset_error, task.exception!!.localizedMessage)
                        }
                        positive.setOnClickListener { askResetPasswordDialog.dismiss() }
                    })
                    viewModel.resetPassword(fragmentView.email_input_signin.text.toString())
                }
                flipper.showNext()
                positive.isEnabled = false
                negative.visibility = View.GONE
            }

        }
        askResetPasswordDialog.show()
    }
}
