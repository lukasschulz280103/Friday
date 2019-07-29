package com.friday.ar.ui.mainactivity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.Theme
import com.friday.ar.activities.FridayActivity
import com.friday.ar.dashboard.DashboardListItem
import com.friday.ar.fragments.dialogFragments.AuthDialog
import com.friday.ar.fragments.dialogFragments.ChangelogDialogFragment
import com.friday.ar.fragments.dialogFragments.UninstallOldAppDialog
import com.friday.ar.fragments.dialogFragments.UnsupportedDeviceDialog
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener
import com.friday.ar.fragments.store.MainStoreFragment
import com.friday.ar.fragments.store.ManagerBottomSheetDialogFragment
import com.friday.ar.list.dashboard.DashboardAdapter
import com.friday.ar.service.OnAccountSyncStateChanged
import com.friday.ar.ui.FeedbackSenderActivity
import com.friday.ar.ui.FullscreenActionActivity
import com.friday.ar.ui.WizardActivity
import com.friday.ar.ui.store.StoreDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.page_main.*
import java.util.*
import kotlin.collections.ArrayList

//TODO move all the work to viewModels
class MainActivity : FridayActivity(), OnAccountSyncStateChanged {
    private var storeFragment = MainStoreFragment()
    internal lateinit var app: FridayApplication
    private lateinit var mOnAuthCompleted: OnAuthCompletedListener
    private lateinit var viewModel: MainActivityViewModel
    private var navselected: BottomNavigationView.OnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.main_nav_dashboard -> main_view_flipper!!.displayedChild = 0
            R.id.main_nav_store -> {
                if (findViewById<View>(R.id.stub_page_store) != null) {
                    stub_page_store.visibility = View.VISIBLE
                    setupSorePage()
                }
                main_view_flipper!!.displayedChild = 1
            }
            R.id.main_nav_profile -> {
                if (findViewById<View>(R.id.stub_page_profile) != null) {
                    stub_page_profile.visibility = View.VISIBLE
                    setupProfilePage()
                }
                main_view_flipper!!.displayedChild = 2
            }
        }
        true
    }
    var authDialogFragment: AuthDialog? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Theme.getCurrentAppTheme(this))
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        app = application as FridayApplication

        viewModel = ViewModelProviders.of(this, MainActivityViewModel.Factory(app)).get(MainActivityViewModel::class.java)

        viewModel.isFirstUse.observe(this, Observer { isFirstUse ->
            if (isFirstUse) {
                val showWizard = Intent(this, WizardActivity::class.java)
                startActivity(showWizard)
            }
        })

        viewModel.energySaverActive.observe(this@MainActivity, Observer { isEnergySaverActive ->
            if (isEnergySaverActive) {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(R.string.energy_saver_warn_title)
                builder.setMessage(R.string.energy_saver_warn_msg)
                builder.setPositiveButton(R.string.deactivate) { _, _ -> }
                builder.setNegativeButton(R.string.later, null)
                builder.create().show()
            }
        })
        viewModel.isUpdatedVersion.observe(this@MainActivity, Observer { isUpdatedVersion ->
            if (isUpdatedVersion) {
                runOnUiThread {
                    val changeLogDialog = ChangelogDialogFragment()
                    changeLogDialog.show(supportFragmentManager, "ChangeLogDialog")
                }
            }
        })

        viewModel.isOldVersionInstalled.observe(this@MainActivity, Observer { isOldVersionInstalled ->
            if (isOldVersionInstalled) {
                val uninstallOldDialogFragment = UninstallOldAppDialog()
                //Using FragmentManager to replace content view with dialog(to make animation work better)
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                        .replace(android.R.id.content, uninstallOldDialogFragment)
                        .commit()
            }
        })

        //Setting up views and objects on separate thread to improve performance at startup
        Thread(Runnable {
            if (resources.getBoolean(R.bool.isTablet)) {
                runOnUiThread { UnsupportedDeviceDialog().show(supportFragmentManager, "UnsupportedDeviceDialog") }
                return@Runnable
            }

            //Apply padding to the main pages content frame so it fits the height of the title view
            val vto = parallaxScollView.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mainTitleView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val height = mainTitleView.measuredHeight
                    runOnUiThread { parallaxScollView.setPadding(0, height, 0, 0) }
                }
            })

            val theme = Theme(this@MainActivity)
            val appThemeIndex = theme.indexOf(Theme.getCurrentAppTheme(this@MainActivity))
            runOnUiThread { mainTitleView.background = theme.createGradient(appThemeIndex) }


            main_bottom_nav.setOnNavigationItemSelectedListener(navselected)

            start_actionmode.setOnClickListener {
                val intent = Intent(this@MainActivity, FullscreenActionActivity::class.java)
                startActivity(intent)
            }

            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
            if (defaultSharedPreferences.getInt("theme", 0) == 0) {
                defaultSharedPreferences.edit().putInt("theme", R.style.AppTheme).apply()
            }
        }).start()
        Handler().postDelayed({ start_actionmode.shrink() }, 2500)
        //app.registerForSyncStateChange(this);
        val dataList = ArrayList<DashboardListItem>()
        mainPageDashboardList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mainPageDashboardList.adapter = DashboardAdapter(this, dataList)
        mainSwipeRefreshLayout.setOnRefreshListener {
            val newData = ArrayList<DashboardListItem>()

        }
    }


    private fun setupSorePage() {
        val storeExpandManagerButton = findViewById<ImageButton>(R.id.storeMore)
        storeExpandManagerButton.setOnClickListener {
            val managerDialog = ManagerBottomSheetDialogFragment(this)
            managerDialog.show(supportFragmentManager, "ManagerBottomSheet")
        }
        supportFragmentManager.beginTransaction()
                .add(R.id.store_frag_container, storeFragment)
                .commit()
    }

    private fun setupProfilePage() {
        val theme = Theme(this@MainActivity)
        val appThemeIndex = theme.indexOf(Theme.getCurrentAppTheme(this@MainActivity))
        findViewById<View>(R.id.profileTitleViewContainer).background = theme.createGradient(appThemeIndex)
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
        if (!storeFragment.isAdded && findViewById<View>(R.id.stub_page_store) == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.store_frag_container, storeFragment)
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (authDialogFragment != null && authDialogFragment!!.isAdded) {
            authDialogFragment!!.dismissDialog()
        } else {
            Snackbar.make(findViewById(R.id.viewflipperparent), getString(R.string.leave_app), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.action_leave)) { finishAffinity() }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FULLSCREEN_REQUEST_CODE && data != null) {
            val errtype = data.getStringExtra("errtype")
            val alertDialog = MaterialAlertDialogBuilder(this)
            alertDialog.setPositiveButton(android.R.string.ok, null)
            alertDialog.setNeutralButton(R.string.app_feedback) { _, _ -> startActivity(Intent(this@MainActivity, FeedbackSenderActivity::class.java)) }
            when (errtype) {
                "TYPE_NOT_INSTALLED" -> {
                    run {
                        alertDialog.setMessage(R.string.errtype_not_installed)
                        val apk = ArCoreApk.getInstance()
                        try {
                            apk.requestInstall(this, true)
                        } catch (e: UnavailableDeviceNotCompatibleException) {
                            data.putExtra("errtype", "TYPE_DEVICE_NOT_SUPPORTED")
                            onActivityResult(requestCode, resultCode, data)
                        } catch (e: UnavailableUserDeclinedInstallationException) {
                            e.printStackTrace()
                        }
                    }
                    run { alertDialog.setMessage(R.string.errtype_arcore_apk_too_old) }
                    run { alertDialog.setMessage(R.string.errtype_sdk_too_old) }
                    run { alertDialog.setMessage(R.string.errtype_device_incompatible) }
                }
                "TYPE_OLD_APK" -> {
                    run { alertDialog.setMessage(R.string.errtype_arcore_apk_too_old) }
                    run { alertDialog.setMessage(R.string.errtype_sdk_too_old) }
                    run { alertDialog.setMessage(R.string.errtype_device_incompatible) }
                }
                "TYPE_OLD_SDK_TOOL" -> {
                    run { alertDialog.setMessage(R.string.errtype_sdk_too_old) }
                    run { alertDialog.setMessage(R.string.errtype_device_incompatible) }
                }
                "TYPE_DEVICE_INCOMPATIBLE" -> {
                    alertDialog.setMessage(R.string.errtype_device_incompatible)
                }
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
            } else {
            }
        }
    }

    fun setmOnAuthCompleted(mOnAuthCompleted: OnAuthCompletedListener) {
        this.mOnAuthCompleted = mOnAuthCompleted
        authDialogFragment!!.onAuthListener = mOnAuthCompleted
    }

    fun promptSignin() {
        Log.d("FirebaseAuth", "showing auth dialog")
        authDialogFragment = AuthDialog()
        //TODO UninitializedPropertyException: property accessed but not initialized
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(android.R.id.content, authDialogFragment!!)
                .commit()
    }

    fun dismissUninstallPrompt() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(android.R.id.content, Fragment())
                .commit()
    }

    fun goToStore() {
        val i = Intent(this, StoreDetailActivity::class.java)
        startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.store_default_toolbar, menu)
        return true
    }

    override fun onSyncStateChanged() {

    }

    companion object {
        const val FULLSCREEN_REQUEST_CODE = 22
        private const val REQUEST_PERMISSIONS_CODE = 900
        private const val LOGTAG = "FridayMainActivity"
    }
}
