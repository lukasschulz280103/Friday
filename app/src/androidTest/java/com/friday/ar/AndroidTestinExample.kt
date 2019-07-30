package com.friday.ar

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.util.cache.CacheUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class UtilityTests {
    lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun cacheUtilTest() {
        val testFile = File(context.getExternalFilesDir("/tests/myplugin.fpl")!!.path)
        val cacheUtil = CacheUtil.cachePluginFile(context, ZippedPluginFile(testFile))
    }
}