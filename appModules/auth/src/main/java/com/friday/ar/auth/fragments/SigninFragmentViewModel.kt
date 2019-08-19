package com.friday.ar.auth.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.friday.ar.Constant
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener
import com.friday.ar.service.AccountSyncService
import com.friday.ar.util.analitycs.AnalyticUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class SigninFragmentViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val LOGTAG = "SignInFragmentViewModel"
    }

    val inputsUsabilityState = MutableLiveData<Boolean>()
    val newUserCretionTask = MutableLiveData<Task<AuthResult>>()
    val onEmailSigninCompleted = MutableLiveData<Task<AuthResult>>()
    val onGoogleSignInCompleted = MutableLiveData<Task<AuthResult>>()
    val onPasswordResetCompleted = MutableLiveData<Task<Void>>()
    val onAuthCompletedListenerList = ArrayList<OnAuthCompletedListener>()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val createNewUserCompleteListener = OnCompleteListener<AuthResult> { task ->
        newUserCretionTask.postValue(task)
        inputsUsabilityState.postValue(true)
        if (task.isSuccessful) {
            task.result!!.user!!.sendEmailVerification()
            AnalyticUtil.logUserCreation(application)
        } else {
            if (task.exception != null) {
                AnalyticUtil.logFailedUserCreation(application, null, task.exception!!.message)
                Crashlytics.logException(task.exception)
            }
        }
    }

    private val onSignInCompletionListener = OnCompleteListener<AuthResult> { task ->
        onEmailSigninCompleted.postValue(task)
        inputsUsabilityState.postValue(true)
        if (task.isSuccessful) {
            notifyAuthCompleted()
            AnalyticUtil.logSuccessfulAuthentication(application, Constant.AnalyticEvent.LOGIN_EVENT_EMAIL)
        } else {
            try {
                throw task.exception!!
            } catch (e: FirebaseAuthEmailException) {
                AnalyticUtil.logFailedAuthentication(application, Constant.AnalyticEvent.LOGIN_EVENT_EMAIL, e.errorCode, e.message)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                //Do nothing, not important for analytics
            } catch (e: FirebaseAuthInvalidUserException) {
                AnalyticUtil.logFailedAuthentication(application, Constant.AnalyticEvent.LOGIN_EVENT_EMAIL, e.errorCode, e.message)
            } catch (e: Exception) {
                Log.e(LOGTAG, "Unknown error occured while user tried to sign in:" + e.message, e)
                AnalyticUtil.logFailedAuthentication(application, Constant.AnalyticEvent.LOGIN_EVENT_EMAIL, null, e.message)
            }
        }
    }

    @SuppressLint("InflateParams")
    fun resetPassword(email: String) {
        val resetPassword = firebaseAuth.sendPasswordResetEmail(email)
        resetPassword.addOnCompleteListener { task ->
            onPasswordResetCompleted.postValue(task)
        }
    }

    fun createUser(email: String, password: String) {
        val crUser = firebaseAuth.createUserWithEmailAndPassword(email, password)
        crUser.addOnCompleteListener(createNewUserCompleteListener)
        inputsUsabilityState.postValue(false)
    }

    fun signinUserWithEmailAndPassword(email: String, password: String) {
        inputsUsabilityState.postValue(false)
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(onSignInCompletionListener)
    }

    fun firebaseAuthWithGoogle(googleAccount: GoogleSignInAccount) {
        inputsUsabilityState.postValue(false)
        Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + googleAccount.id!!)
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    onGoogleSignInCompleted.postValue(task)
                    if (task.isSuccessful) {
                        Crashlytics.setUserIdentifier(firebaseAuth.currentUser!!.uid)
                        Crashlytics.setUserEmail(firebaseAuth.currentUser!!.email)
                        Crashlytics.setUserName(firebaseAuth.currentUser!!.displayName)

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                        val jobScheduler = getApplication<Application>().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                        val jobInfo = JobInfo.Builder(Constant.Jobs.JOB_SYNC_ACCOUNT, ComponentName(getApplication(), AccountSyncService::class.java))
                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                .setBackoffCriteria(60000 * 5, JobInfo.BACKOFF_POLICY_LINEAR)
                                .build()
                        jobScheduler.schedule(jobInfo)
                        notifyAuthCompleted()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    }
                }
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
}