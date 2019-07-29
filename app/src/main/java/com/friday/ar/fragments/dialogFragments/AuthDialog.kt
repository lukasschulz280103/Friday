package com.friday.ar.fragments.dialogFragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.friday.ar.R
import com.friday.ar.fragments.DefaultSignInFragment
import kotlinx.android.synthetic.main.signin_layout.view.*

class AuthDialog : FullscreenDialog() {
    private lateinit var mContext: Context
    val signInFragment: DefaultSignInFragment = DefaultSignInFragment()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dialogView = inflater.inflate(R.layout.signin_layout, container, false)
        dialogView.close_signin_dialog.setOnClickListener { dismiss() }
        return dialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager
                .beginTransaction()
                .replace(R.id.signin_fragment_container, signInFragment)
                .commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun dismiss() {
        (mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view!!.windowToken, 0)
        super.dismiss()
    }

}
