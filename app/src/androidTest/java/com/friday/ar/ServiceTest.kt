package com.friday.ar

import android.content.Context
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.friday.ar.pluginsystem.service.PluginLoader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ServiceTest {
    lateinit var context: Context
    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
    }

    @Test
    fun pluginLoaderServiceTest() {
        val pluginLoader = PluginLoader(context)
        pluginLoader.startLoading()
    }
}