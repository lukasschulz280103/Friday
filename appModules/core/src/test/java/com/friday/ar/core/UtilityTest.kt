package com.friday.ar.core

import com.friday.ar.core.di.coreModule
import com.friday.ar.core.util.validation.Validator
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject

open class UtilityTest : KoinTest {

    val validator by inject<Validator>()

    @Test
    fun utilityTest() {
        startKoin {
            modules(coreModule)
        }

        val fakeEmail = "test.mailgmx.de"
        val validEmail = "test.mail@gmx.de"
        assertEquals(validator.validateEmail(fakeEmail), false)
        assertEquals(validator.validateEmail(validEmail), true)
    }

}