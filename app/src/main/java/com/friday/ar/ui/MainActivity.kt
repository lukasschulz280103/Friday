package com.friday.ar.ui


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PowerManager
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.Theme
import com.friday.ar.activities.FridayActivity
import com.friday.ar.fragments.dialogFragments.AuthDialog
import com.friday.ar.fragments.dialogFragments.ChangelogDialogFragment
import com.friday.ar.fragments.dialogFragments.UninstallOldAppDialog
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener
import com.friday.ar.fragments.store.MainStoreFragment
import com.friday.ar.fragments.store.ManagerBottomSheetDialogFragment
import com.friday.ar.service.OnAccountSyncStateChanged
import com.friday.ar.ui.store.StoreDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : FridayActivity(), OnAccountSyncStateChanged {
    internal var storeFragment = MainStoreFragment()
    internal lateinit var app: FridayApplication
    internal lateinit var mOnAuthCompleted: OnAuthCompletedListener
    private var viewSwitcherMain: ViewFlipper? = null
    internal var navselected: BottomNavigationView.OnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.main_nav_dashboard -> viewSwitcherMain!!.displayedChild = 0
            R.id.main_nav_store -> {
                if (findViewById<View>(R.id.stub_page_store) != null) {
                    stub_page_store.visibility = View.VISIBLE
                    setupSorePage()
                }
                viewSwitcherMain!!.displayedChild = 1
            }
            R.id.main_nav_profile -> {
                if (findViewById<View>(R.id.stub_page_profile) != null) {
                    stub_page_profile.visibility = View.VISIBLE
                    setupProfilePage()
                }
                viewSwitcherMain!!.displayedChild = 2
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

        //Setting up views and objects on separate thread to improve performance at startup
        Thread(Runnable {
            viewSwitcherMain = findViewById(R.id.main_view_flipper)
            val parallaxScrollView = findViewById<FrameLayout>(R.id.parallaxScollView)
            val mainTitleView = findViewById<LinearLayout>(R.id.mainTitleView)
            app = application as FridayApplication
            //Apply padding to the main pages content frame so it fits the height of the title view
            val vto = parallaxScrollView.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mainTitleView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val height = mainTitleView.measuredHeight
                    runOnUiThread { parallaxScrollView.setPadding(0, height, 0, 0) }
                }
            })

            val theme = Theme(this@MainActivity)
            val appThemeIndex = theme.indexOf(Theme.getCurrentAppTheme(this@MainActivity))
            runOnUiThread { findViewById<View>(R.id.mainTitleView).background = theme.createGradient(appThemeIndex) }


            (findViewById<View>(R.id.main_bottom_nav) as BottomNavigationView).setOnNavigationItemSelectedListener(navselected)

            findViewById<View>(R.id.start_actionmode).setOnClickListener { v ->
                val intent = Intent(this@MainActivity, FullscreenActionActivity::class.java)
                startActivity(intent)
            }

            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm != null) {
                if (pm.isPowerSaveMode) {
                    val builder = MaterialAlertDialogBuilder(this@MainActivity)
                    builder.setTitle(R.string.energy_saver_warn_title)
                    builder.setMessage(R.string.energy_saver_warn_msg)
                    builder.setPositiveButton(R.string.deactivate) { dialogInterface, i -> }
                    builder.setNegativeButton(R.string.later, null)
                    runOnUiThread { builder.create().show() }
                }
            }

            val default_pref = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
            val pkgInf: PackageInfo
            try {
                pkgInf = packageManager.getPackageInfo(packageName, 0)
                if (default_pref.getString("version", "0") != pkgInf.versionName || default_pref.getBoolean("pref_devmode_show_changelog", false)) {
                    val changelogdialog = ChangelogDialogFragment()
                    changelogdialog.show(supportFragmentManager, "ChangeLogDialog")
                    val editor = default_pref.edit()
                    editor.putString("version", pkgInf.versionName)
                    editor.apply()
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            checkForFirstUse()

            try {
                val isOldAppInstalled = packageManager
                isOldAppInstalled.getPackageInfo("com.code_design_camp.client.rasberrypie.rbpieclient", PackageManager.GET_ACTIVITIES)
                val uninstallOldDialogFragment = UninstallOldAppDialog()
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                        .replace(android.R.id.content, uninstallOldDialogFragment)
                        .commit()
            } catch (e: PackageManager.NameNotFoundException) {
                Log.i(LOGTAG, "no old version found")
            }

            if (default_pref.getInt("theme", 0) == 0) {
                default_pref.edit().putInt("theme", R.style.AppTheme).apply()
            }
        }).start()
        //app.registerForSyncStateChange(this);

    }

    private fun setupSorePage() {
        val storeExpandManagerButton = findViewById<ImageButton>(R.id.storeMore)
        storeExpandManagerButton.setOnClickListener { v ->
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

    private fun checkForFirstUse() {
        val settingsfile = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        if (settingsfile.getBoolean("isFirstUse", true)) {
            val notifieffirstuse = MaterialAlertDialogBuilder(this@MainActivity)
            notifieffirstuse.setTitle("Welcome to friday")
            notifieffirstuse.setMessage("Thank you for downloading friday.\n\nRemember that you are in a pre-release of our app - Some features may not work properly or this app will crash at some points.")
            notifieffirstuse.setPositiveButton(android.R.string.ok, null)
            notifieffirstuse.create().show()
            settingsfile.edit().putBoolean("isFirstUse", false).apply()
            val showWizard = Intent(this, WizardActivity::class.java)
            startActivity(showWizard)
        }
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
                    .setAction(getString(R.string.action_leave)) { view -> finishAffinity() }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FULLSCREEN_REQUEST_CODE && data != null) {
            val errtype = data.getStringExtra("errtype")
            val alertDialog = MaterialAlertDialogBuilder(this)
            alertDialog.setPositiveButton(android.R.string.ok, null)
            alertDialog.setNeutralButton(R.string.app_feedback) { dialogInterface, i -> startActivity(Intent(this@MainActivity, FeedbackSenderActivity::class.java)) }
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
                        .setPositiveButton(R.string.retry) { dialogInterface, which -> requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSIONS_CODE) }
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
        authDialogFragment!!.onAuthListener = mOnAuthCompleted
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

    fun goToStore(v: View) {
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
