package com.friday.ar.core.util.validation

object Validator {
    fun validateEmail(email: String): Boolean {
        return "" != email && email.contains("@") && email.contains(".")
    }
}
