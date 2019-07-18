package com.friday.ar.util

object Validator {

    //TODO validate email via RegEx
    fun validateEmail(email: String): Boolean {
        return "" != email && email.contains("@") && email.contains(".")
    }
}
