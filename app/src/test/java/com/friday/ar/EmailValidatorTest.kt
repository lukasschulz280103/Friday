package com.friday.ar

import com.friday.ar.core.util.validation.Validator
import org.junit.Test
import kotlin.test.assertEquals

class UtilityTesting {

    @Test
    fun testEmailValidation() {
        assertEquals(Validator.validateEmail("lukasfaber"), false)
        assertEquals(Validator.validateEmail("test.email@myprovider.com"), true)
    }
}