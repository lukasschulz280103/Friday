package com.friday.ar.util

object Validator {
    fun validateEmail(email: String): Boolean {
        return "" != email && email.contains("@") && email.contains(".")
    }
}
