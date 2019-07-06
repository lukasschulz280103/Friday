package com.friday.ar.util

import android.content.Context
import android.widget.EditText

import com.friday.ar.R

object Validator {
    fun validateEmailInput(c: Context, email: EditText): Boolean {
        val text = email.text.toString()
        val isValid = validateEmail(text)
        if (!isValid) {
            email.error = c.getString(R.string.mail_invalid_error)
        }
        return isValid
    }

    fun validateEmail(email: String): Boolean {
        return "" != email && email.contains("@") && email.contains(".")
    }
}
