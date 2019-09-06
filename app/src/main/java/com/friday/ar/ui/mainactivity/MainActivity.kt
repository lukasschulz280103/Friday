package com.friday.ar.ui.mainactivity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.ImageButton
import androidx.lifecycle.Observer
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.base.ui.FullscreenActionActivity
import com.friday.ar.core.Constant
import com.friday.ar.core.Theme
import com.friday.ar.core.activity.FridayActivity
import com.friday.ar.core_ui.recyclerview.layoutmanager.ScrollableLayoutmanager
import com.friday.ar.dashboard.list.DashboardAdapter
import com.friday.ar.feedback.ui.FeedbackSenderActivity
import com.friday.ar.fragments.dialogFragments.UninstallOldAppDialog
import com.friday.ar.fragments.dialogFragments.UnsupportedDeviceDialog
import com.friday.ar.fragments.dialogFragments.changelog.ChangelogDialogFragment
import com.friday.ar.store.ui.StoreDetailActivity
import com.friday.ar.store.ui.fragments.MainStoreFragment
import com.friday.ar.store.ui.fragments.ManagerBottomSheetDialogFragment
import com.friday.ar.wizard.ui.WizardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.ArCoreApk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.page_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

@Suppress("unused")
class MainActivity : FridayActivity() {
    companion object {
        const val FULLSCREEN_REQUEST_CODE = 22
        private const val REQUEST_PERMISSIONS_CODE = 900
        private const val LOGTAG = "FridayMainActivity"
        private const val SITE_DASHBOARD = 0
        private const val SITE_STORE = 1
        private const val SITE_PROFILE = 2
    }

    private var storeFragment = MainStoreFragment()
    internal lateinit var app: FridayApplication
    private val viewModel by viewModel<MainActivityViewModel>()

    private var navselected: BottomNavigationView.OnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.main_nav_dashboard -> {
                main_view_flipper!!.displayedChild = SITE_DASHBOARD
                start_actionmode.extend()
            }
            R.id.main_nav_store -> {
                if (findViewById<View>(R.id.stub_page_store) != null) {
                    stub_page_store.visibility = View.VISIBLE
                    setupSorePage()
                }
                main_view_flipper!!.displayedChild = SITE_STORE
                start_actionmode.shrink()
            }
            R.id.main_nav_profile -> {
                if (findViewById<View>(R.id.stub_page_profile) != null) {
                    stub_page_profile.visibility = View.VISIBLE
                    setupProfilePage()
                }
                main_view_flipper!!.displayedChild = SITE_PROFILE
                start_actionmode.shrink()
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        app = application as FridayApplication

        setContentView(R.layout.activity_main)

        if (resources.getBoolean(R.bool.isTablet)) {
            UnsupportedDeviceDialog().show(supportFragmentManager, "UnsupportedDeviceDialog")
            return
        }

        viewModel.getFirstUseState().observe(this, Observer { isFirstUse ->
            if (isFirstUse) {
                val showWizard = Intent(this, WizardActivity::class.java)
                startActivity(showWizard)
            }
        })

        viewModel.getEnergySaverState().observe(this@MainActivity, Observer { isEnergySaverActive ->
            if (isEnergySaverActive) {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(R.string.energy_saver_warn_title)
                builder.setMessage(R.string.energy_saver_warn_msg)
                builder.setPositiveButton(R.string.deactivate) { _, _ -> }
                builder.setNegativeButton(R.string.later, null)
                builder.create().show()
            }
        })
        viewModel.getIsUpdatedVersion().observe(this@MainActivity, Observer { isUpdatedVersion ->
            if (isUpdatedVersion) {
                val changeLogDialog = ChangelogDialogFragment()
                changeLogDialog.show(supportFragmentManager, "ChangeLogDialog")
            }
        })

        viewModel.getOldVersionInstalledState().observe(this@MainActivity, Observer { pair ->
            val isOldVersionInstalled = pair.first
            if (isOldVersionInstalled) {
                val dialog = UninstallOldAppDialog()
                val bundle = Bundle()
                Log.d("oldversion", pair.second!!)
                bundle.putString("packageName", pair.second)
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, "UninstallOldAppDialog")
            }
        })

        //Setting up views and objects on separate thread to improve performance at startup
        Thread(Runnable {
            val theme = Theme(this@MainActivity)
            runOnUiThread { mainTitleView.background = theme.createAppThemeGadient() }


            main_bottom_nav.setOnNavigationItemSelectedListener(navselected)

            start_actionmode.setOnClickListener {
                val intent = Intent(this@MainActivity, FullscreenActionActivity::class.java)
                startActivityForResult(intent, FULLSCREEN_REQUEST_CODE)
                Answers.getInstance().logCustom(CustomEvent(Constant.AnalyticEvent.CUSTOM_EVENT_ACTIONMODE))
            }
        }).start()

        mainPageDashboardList.layoutManager = ScrollableLayoutmanager(this)
        mainPageDashboardList.adapter = DashboardAdapter(this, ArrayList(0))

        mainSwipeRefreshLayout.setOnRefreshListener {
            viewModel.runRefresh()
        }

        viewModel.getDashBoardListData().observe(this, Observer { list ->
            mainSwipeRefreshLayout.isRefreshing = false
            (mainPageDashboardList.adapter!! as DashboardAdapter).onRefresh(list)
            if (list.isEmpty()) {
                mainPageDashboardList.visibility = View.GONE
                dashboardEmptyView.visibility = View.VISIBLE
            } else {
                mainPageDashboardList.visibility = View.VISIBLE
                dashboardEmptyView.visibility = View.GONE
            }
        })

        mainSwipeRefreshLayout.isRefreshing = true
    }


    private fun setupSorePage() {
        val storeExpandManagerButton = findViewById<ImageButton>(R.id.storeMore)
        storeExpandManagerButton.setOnClickListener {
            val managerDialog = ManagerBottomSheetDialogFragment()
            managerDialog.show(supportFragmentManager, "ManagerBottomSheet")
        }
        supportFragmentManager.beginTransaction()
                .add(R.id.store_frag_container, storeFragment)
                .commit()
    }

    private fun setupProfilePage() {
        val theme = Theme(this@MainActivity)
        findViewById<View>(R.id.profileTitleViewContainer).background = theme.createAppThemeGadient()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        //Fragments need to be removed before saving the instance state
        supportFragmentManager.beginTransaction()
                .remove(storeFragment)
                .commitAllowingStateLoss()
        super.onSaveInstanceState(outState)
    }

    public override fun onResume() {
        super.onResume()
        viewModel.checkForFirstUse()
        if (!storeFragment.isAdded && findViewById<View>(R.id.stub_page_store) == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.store_frag_container, storeFragment)
                    .commit()
        }
    }

    override fun onBackPressed() {
        Snackbar.make(findViewById(R.id.viewflipperparent), getString(R.string.leave_app), Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.action_leave)) { finishAndRemoveTask() }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FULLSCREEN_REQUEST_CODE && data != null) {
            val errtype = data.extras!!.getString("errtype")
            val alertDialog = MaterialAlertDialogBuilder(this)
            alertDialog.setPositiveButton(android.R.string.ok, null)
            alertDialog.setNeutralButton(R.string.app_feedback) { _, _ -> startActivity(Intent(this@MainActivity, FeedbackSenderActivity::class.java)) }
            when (ArCoreApk.Availability.valueOf(errtype!!)) {
                ArCoreApk.Availability.UNKNOWN_ERROR -> alertDialog.setMessage(R.string.unknown_error)
                ArCoreApk.Availability.UNKNOWN_CHECKING -> return
                ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> return
                ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> alertDialog.setMessage(R.string.errtype_arcore_device_not_capable)
                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                    alertDialog.setMessage(R.string.errtype_not_installed)
                    alertDialog.setPositiveButton(R.string.pluginInstaller_activity_installNow) { _, _ -> ArCoreApk.getInstance().requestInstall(this@MainActivity, true) }
                    alertDialog.setNegativeButton(android.R.string.cancel, null)
                }
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> alertDialog.setMessage(R.string.errtype_arcore_apk_too_old)
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> alertDialog.setMessage(R.string.errtype_sdk_too_old)
            }
            alertDialog.create().show()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (!Arrays.equals(grantResults, intArrayOf(PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED))) {
                val permissionWarnDialog = MaterialAlertDialogBuilder(this)
                permissionWarnDialog.setTitle(R.string.err_missing_permissions)
                        .setMessage(R.string.err_permission_need_explanation)
                        .setIcon(R.drawable.ic_twotone_security_24px)
                        .setCancelable(false)
                        .setPositiveButton(R.string.retry) { _, _ -> requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSIONS_CODE) }
                        .create().show()
            }

        }
    }

    fun goToStore() {
        val i = Intent(this, StoreDetailActivity::class.java)
        startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.store_default_toolbar, menu)
        return true
    }

    fun goToStore(view: View) {}
}
