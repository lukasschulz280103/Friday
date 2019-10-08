package com.friday.ar.store.ui.storeInstallationManagerActivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.friday.ar.core.Constant
import com.friday.ar.core.activity.FridayActivity
import com.friday.ar.core.util.DisplayUtil
import com.friday.ar.store.R
import com.friday.ar.store.ui.adapter.PluginListAdapter
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_store_installation_manager.*
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel

class StoreInstallationManagerActivity : FridayActivity() {
    companion object {
        private const val LOGTAG = "StoreInstallations"
        const val OPEN_PLUGIN_INTENT_CODE = 733
    }

    private val viewModel by viewModel<StoreInstallationsManagerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_store_installation_manager)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        appList.adapter = PluginListAdapter(this, null)

        viewModel.pluginListData.observe(this, Observer { pluginList ->
            (appList.adapter as PluginListAdapter).onRecieveUpdatedData(pluginList)
            Log.d(LOGTAG, "length of plugin list: ${pluginList.size}")
            setEmptyViewVisibleByInt(pluginList.size)
        })

        registerForContextMenu(appList)

        appList.layoutManager = LinearLayoutManager(this)
    }

    private fun setEmptyViewVisibleByInt(dataSize: Int) {
        if (dataSize == 0) {
            emptyView.visibility = View.VISIBLE
            appList.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            appList.visibility = View.VISIBLE
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
                val sharedPreferences: SharedPreferences = get()
                if (sharedPreferences.getBoolean(Constant.PreferenceKeys.Store.INSTALLER_SHOW_DISK_INSTALL_WARNING, true)) {
                    val dontShowAgainCheck = MaterialCheckBox(this)
                    val contentFrame = FrameLayout(this)
                    val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.marginStart = DisplayUtil.dpToPx(16)
                    layoutParams.marginEnd = DisplayUtil.dpToPx(16)
                    dontShowAgainCheck.setText(R.string.dont_show_again)
                    dontShowAgainCheck.setOnCheckedChangeListener { _, isChecked -> sharedPreferences.edit().putBoolean(Constant.PreferenceKeys.Store.INSTALLER_SHOW_DISK_INSTALL_WARNING, !isChecked).apply() }
                    contentFrame.addView(dontShowAgainCheck, layoutParams)
                    val warnDialog = MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.security_warning_title)
                            .setMessage(R.string.storeInstallationsManager_diskInstallWarningMessage)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                startFilePicker()
                            }
                            .setView(contentFrame)
                            .setCancelable(false)
                            .create()
                    warnDialog.show()

                } else {
                    startFilePicker()
                }
            }
        }
        return true
    }

    private fun startFilePicker() {
        val selectPluginIntent = Intent()
        selectPluginIntent.action = Intent.ACTION_GET_CONTENT
        selectPluginIntent.type = "*/*"
        startActivityForResult(selectPluginIntent, OPEN_PLUGIN_INTENT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_PLUGIN_INTENT_CODE && resultCode == RESULT_OK && data != null) {

            viewModel.install(contentResolver.openInputStream(data.data!!)!!)
        }
    }
}
