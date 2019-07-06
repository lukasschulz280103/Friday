package com.friday.ar.fragments.templateClasses

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

import com.friday.ar.fragments.interfaces.OnAuthCompletedListener


class AuthFragment : Fragment(), OnAuthCompletedListener {
    override fun onAuthCompleted() {

    }

    override fun onCanceled() {

    }

    inner class AuthDialogFragment : DialogFragment(), OnAuthCompletedListener {

        override fun onAuthCompleted() {

        }

        override fun onCanceled() {

        }
    }
}
