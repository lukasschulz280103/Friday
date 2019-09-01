package com.friday.ar.ui.store.storeInstallationManagerActivity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.friday.ar.R
import com.friday.ar.activities.FridayActivity
import com.friday.ar.list.store.PluginListAdapter
import com.friday.ar.util.DisplayUtil
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_store_installation_manager.*
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
