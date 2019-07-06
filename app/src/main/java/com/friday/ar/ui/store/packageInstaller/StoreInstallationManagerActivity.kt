package com.friday.ar.ui.store.packageInstaller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.Theme
import com.friday.ar.activities.FridayActivity
import com.friday.ar.list.store.PluginListAdapter
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.installer.PluginInstaller
import com.friday.ar.ui.FileSelectorActivity
import com.friday.ar.util.DisplayUtil
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_store_installation_manager.*
import net.lingala.zip4j.exception.ZipException
import java.io.File
import java.io.IOException
import java.util.*

class StoreInstallationManagerActivity : FridayActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Theme.getCurrentAppTheme(this))
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_store_installation_manager)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val pluginLoader = (application as FridayApplication).applicationPluginLoader
        appList.layoutManager = LinearLayoutManager(this)
        if (pluginLoader!!.indexedPlugins.size == 0) {
            emptyView.visibility = View.VISIBLE
            appList.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            appList.visibility = View.VISIBLE
            appList.adapter = PluginListAdapter(this, pluginLoader.indexedPlugins)
            registerForContextMenu(appList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.store_installed_plugins, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
            }
            R.id.install_from_disk -> {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                if (sharedPreferences.getBoolean("storeInstallationsManager_showSecurityWarning", true)) {
                    val dontShowAgainCheck = MaterialCheckBox(this)
                    val contentFrame = FrameLayout(this)
                    val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.marginStart = DisplayUtil.dpToPx(16)
                    layoutParams.marginEnd = DisplayUtil.dpToPx(16)
                    dontShowAgainCheck.setText(R.string.dont_show_again)
                    dontShowAgainCheck.setOnCheckedChangeListener { _, isChecked -> sharedPreferences.edit().putBoolean("storeInstallationsManager_showSecurityWarning", !isChecked).apply() }
                    contentFrame.addView(dontShowAgainCheck, layoutParams)
                    val warnDialog = MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.storeInstallationsManager_diskInstallWarningTitle)
                            .setMessage(R.string.storeInstallationsManager_diskInstallWarningMessage)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                val selectPluginIntent = Intent(this@StoreInstallationManagerActivity, FileSelectorActivity::class.java)
                                startActivityForResult(selectPluginIntent, OPEN_PLUGIN_INTENT_CODE)
                            }
                            .setView(contentFrame)
                            .setCancelable(false)
                            .create()
                    warnDialog.show()

                } else {
                    val selectPluginIntent = Intent(this, FileSelectorActivity::class.java)
                    startActivityForResult(selectPluginIntent, OPEN_PLUGIN_INTENT_CODE)
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_PLUGIN_INTENT_CODE && resultCode == RESULT_OK && data != null) {
            Log.d(LOGTAG, "data:" + data.data!!.toString())
            val openedFile = File(Objects.requireNonNull(data.data!!.path))
            val installer = PluginInstaller(this)
            installer.setOnInstallProgressChangedListener(object : PluginInstaller.OnInstallProgressChangedListener {
                override fun onProgressChanged(progressMessage: String) {
                    Log.d(LOGTAG, "progress changed")
                }

                override fun onSuccess() {
                    Log.d(LOGTAG, "success")
                }

                override fun onFailure() {
                    Log.e(LOGTAG, "failure")
                }
            })
            Thread {
                try {
                    installer.installFrom(ZippedPluginFile(File(openedFile.path)))
                } catch (e: IOException) {
                    Log.e(LOGTAG, e.localizedMessage, e)
                } catch (e: ZipException) {
                    Log.e(LOGTAG, e.localizedMessage, e)
                }


            }.start()
        }
    }

    companion object {
        private val LOGTAG = "StoreInstallations"
        val OPEN_PLUGIN_INTENT_CODE = 733
    }
}
