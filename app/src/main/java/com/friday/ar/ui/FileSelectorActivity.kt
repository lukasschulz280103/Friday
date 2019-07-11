package com.friday.ar.ui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.Theme
import com.friday.ar.activities.FridayActivity
import com.friday.ar.plugin.PluginVerticalListAdapter
import com.google.android.material.appbar.AppBarLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.io.File
import java.util.Arrays
import kotlin.Comparator

//TODO:Add support for selecting files
class FileSelectorActivity : FridayActivity() {
    private var fileListRecyclerView: RecyclerView? = null
    private var slidingUpPanelLayout: SlidingUpPanelLayout? = null
    private var openPlugin: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Theme.getCurrentAppTheme(this))
        super.onCreate(savedInstanceState)
        requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), 8001)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_file_selector)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        supportActionBar!!.setTitle(R.string.open_plugin_file)
        slidingUpPanelLayout = findViewById(R.id.contentPane)
        val indexingStatus = findViewById<TextView>(R.id.indexing_status)
        openPlugin = findViewById(R.id.select_file)
        setResult(Activity.RESULT_CANCELED)
        val app = application as FridayApplication
        indexingStatus.text = if (app.indexedInstallablePluginFiles.size != 0)
            resources.getQuantityString(R.plurals.pluginInstaller_indexingStatus, app.indexedInstallablePluginFiles.size, app.indexedInstallablePluginFiles.size)
        else
            getString(R.string.pluginInstaller_noItemsIndexed)
        slidingUpPanelLayout!!.setDragView(R.id.buttonbar)
        openPlugin!!.setOnClickListener { finish() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 8001 && Arrays.equals(grantResults, intArrayOf(PERMISSION_GRANTED))) {
            val fileListLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            fileListRecyclerView = findViewById(R.id.fileList)
            fileListRecyclerView!!.layoutManager = fileListLayoutManager
            fileListRecyclerView!!.adapter = FileSystemArrayAdapter(Environment.getExternalStorageDirectory())
            val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
            val stateListAnimator = StateListAnimator()
            fileListRecyclerView!!.setOnScrollChangeListener { _, _, _, _, _ ->
                if (fileListLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(appBarLayout, "elevation", 0f))
                    appBarLayout.stateListAnimator = stateListAnimator
                } else {
                    val stateListAnimator1 = StateListAnimator()
                    stateListAnimator1.addState(IntArray(0), ObjectAnimator.ofFloat(appBarLayout, "elevation", 8f))
                    appBarLayout.stateListAnimator = stateListAnimator1
                }
            }
            val indexedFilesList = findViewById<RecyclerView>(R.id.indexedFilesList)
            indexedFilesList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            indexedFilesList.adapter = PluginVerticalListAdapter(this, (application as FridayApplication).indexedInstallablePluginFiles)
        }
    }

    override fun onBackPressed() {
        val fileListAdapter = fileListRecyclerView!!.adapter as FileSystemArrayAdapter?
        if (slidingUpPanelLayout!!.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else if (fileListAdapter?.sourceFile!!.parentFile != null && fileListAdapter.sourceFile!!.parentFile.canRead()) {
            Log.d(LOGTAG, "parent:" + fileListAdapter.sourceFile!!.parentFile.path)
            fileListAdapter.setDirectoryList(fileListAdapter.sourceFile!!.parentFile)
        } else {
            super.onBackPressed()
        }
    }

    internal inner class FileSystemArrayAdapter(startFrom: File) : RecyclerView.Adapter<FileViewHolder>() {
        var sourceFile: File? = null
        private lateinit var directoryList: List<File>

        init {
            Log.d(LOGTAG, "starting file array adapter")
            setDirectoryList(startFrom)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
            return FileViewHolder(LayoutInflater.from(applicationContext).inflate(R.layout.file_selector_list_item, parent, false))
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
            Log.d(LOGTAG, "creating item $position")
            val directoryFileItem = directoryList[position]
            holder.fileName.text = directoryFileItem.name
            if (directoryFileItem.isDirectory && !directoryFileItem.isFile) {
                holder.fileIcon.setImageDrawable(getDrawable(R.drawable.ic_twotone_folder_24px))
                holder.fileSize.visibility = View.GONE
                openPlugin!!.isEnabled = false
                holder.root.setOnClickListener {
                    setResult(Activity.RESULT_CANCELED)
                    Log.d(LOGTAG, directoryFileItem.path)
                    if (directoryFileItem.canRead()) setDirectoryList(directoryFileItem)
                }
            } else {
                //TODO: Add visible sign that marks an file as selected
                holder.fileIcon.setImageDrawable(getDrawable(R.drawable.ic_twotone_insert_drive_file_24px))
                holder.root.setOnClickListener {
                    openPlugin!!.isEnabled = true
                    holder.check.visibility = View.VISIBLE
                    holder.check.animate().scaleX(1f).scaleY(1f).setDuration(200).setInterpolator(DecelerateInterpolator()).start()
                    val resultIntent = Intent()
                    resultIntent.data = Uri.fromFile(directoryFileItem)
                    setResult(Activity.RESULT_OK, resultIntent)
                }
            }

        }

        override fun getItemCount(): Int {
            return sourceFile!!.listFiles().size
        }

        fun setDirectoryList(directoryFile: File) {
            if (sourceFile != null) {
                notifyItemRangeRemoved(0, if (sourceFile!!.listFiles() != null) sourceFile!!.listFiles().size else 0)
            }
            sourceFile = directoryFile
            directoryList = Arrays.asList(*directoryFile.listFiles())
            directoryList.sortedWith(Comparator { file1: File, file2: File ->
                if (file1.isDirectory && file2.isFile)
                    return@Comparator -1
                if (file1.isDirectory && file2.isDirectory) {
                    return@Comparator 0
                }
                if (file1.isFile && file2.isFile) {
                    return@Comparator 0
                }
                1
            }
            )
            notifyItemRangeInserted(0, directoryList.size)
        }
    }

    internal inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var root: LinearLayout = itemView.findViewById(R.id.itemRootLayout)
        var fileIcon: ImageView = itemView.findViewById(R.id.fileIcon)
        var check: ImageView = itemView.findViewById(R.id.check)
        var fileName: TextView = itemView.findViewById(R.id.fileName)
        var fileSize: TextView = itemView.findViewById(R.id.fileSize)

    }

    companion object {
        private const val LOGTAG = "FileSelector"
    }
}
