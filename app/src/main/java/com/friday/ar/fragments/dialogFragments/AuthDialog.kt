package com.friday.ar.fragments.dialogFragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

import com.friday.ar.R
import com.friday.ar.fragments.DefaultSignInFragment
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener

class AuthDialog : DialogFragment() {
    private var dismissdialog = View.OnClickListener { dismissDialog() }
    private val signInFragment: DefaultSignInFragment = DefaultSignInFragment()

    var onAuthListener: OnAuthCompletedListener?
        get() = signInFragment.getmOnAuthCompletedListener()
        set(mOnAuthListener) = signInFragment.setOnAuthCompletedListener(mOnAuthListener!!)

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    fun dismissDialog() {
        fragmentManager!!.beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(android.R.id.content, Fragment())
                .commitNow()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signin_layout, container, false)
        val dismissButton = v.findViewById<ImageButton>(R.id.close_signin_dialog)
        dismissButton.setOnClickListener(dismissdialog)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager
                .beginTransaction()
                .replace(R.id.signin_fragment_container, signInFragment)
                .commit()
    }

}
