package com.friday.ar.pluginsystem

import com.friday.ar.pluginsystem.file.PluginFile
import com.friday.ar.pluginsystem.security.PluginVerifier
import com.friday.ar.pluginsystem.security.VerificationSecurityException
import junit.framework.Assert.assertEquals
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.koin.test.KoinTest
import java.io.File
import java.io.IOException


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PluginFileTest : KoinTest {

    val testValidJSON = JSONObject("{'meta':{}}")
    val metaObj = testValidJSON.getJSONObject("meta")

    @get:Rule
    var folder = TemporaryFolder()

    @Test
    fun testManifest() {

        val testZipFileInTemp = folder.newFile("testPlugin.zip")

        val testPluginMetaFolder = folder.newFolder("testPluginContent", "meta")


        val testZipFileManifest = folder.newFile("testPluginContent/meta/manifest.json")


        testZipFileManifest.writeText(testValidJSON.toString())

        val testPluginZip = ZipFile(testPluginMetaFolder)

        if (!testPluginZip.file.exists()) testPluginZip.createZipFile(File("testPluginContent"), ZipParameters())
        else testPluginZip.addFolder(folder.root.path + "/testPluginContent", ZipParameters())

        testPluginZip.extractAll(folder.newFolder("extractedZip").path)

        val extractedFile = PluginFile("extractedZip")

        var isSuccessful = false

        val verifier = PluginVerifier()
        verifier.setOnVerificationCompleteListener(object : PluginVerifier.OnVerificationCompleteListener {
            override fun onSuccess() {
                isSuccessful = true
            }

            override fun onZipException(e: ZipException) {

            }

            override fun onIOException(e: IOException) {

            }

            override fun onJSONException(e: JSONException) {

            }

            override fun onVerificationFailed(e: VerificationSecurityException) {

            }

        })
        verifier.verify(extractedFile, true)

        assertEquals(isSuccessful, true)
    }

    @Before
    fun initJSON() {
        metaObj.put("applicationName", "TestPlugin")
        metaObj.put("authorName", "Siemens Intelligence Co.")
        metaObj.put("versionName", "1.2.0")
    }

    @After
    fun clearTemp() {
        folder.root.deleteRecursively()
    }
}
