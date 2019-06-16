package com.friday.ar.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.activities.FridayActivity;
import com.friday.ar.plugin.PluginVerticalListAdapter;
import com.friday.ar.util.FileUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.friday.ar.service.PluginIndexer.verify;

//TODO:Add support for selecting files
public class FileSelectorActivity extends FridayActivity {
    private static final String LOGTAG = "FileSelector";
    private RecyclerView fileList;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private Button openPlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 8001);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_file_selector);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.open_plugin_file);
        slidingUpPanelLayout = findViewById(R.id.contentPane);
        TextView indexingStatus = findViewById(R.id.indexing_status);
        openPlugin = findViewById(R.id.select_file);
        setResult(RESULT_CANCELED);
        FridayApplication app = (FridayApplication) getApplication();
        indexingStatus.setText(app.getIndexedFiles().size() != 0 ?
                getResources().getQuantityString(R.plurals.pluginInstaller_indexingStatus, app.getIndexedFiles().size(), app.getIndexedFiles().size()) :
                getString(R.string.pluginInstaller_noItemsIndexed));
        slidingUpPanelLayout.setDragView(R.id.buttonbar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 8001 && Arrays.equals(grantResults, new int[]{PERMISSION_GRANTED})) {
            LinearLayoutManager fileListLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            fileList = findViewById(R.id.fileList);
            fileList.setLayoutManager(fileListLayoutManager);
            fileList.setAdapter(new FileSystemArrayAdapter(Environment.getExternalStorageDirectory()));
            AppBarLayout appBarLayout = findViewById(R.id.appbar);
            StateListAnimator stateListAnimator = new StateListAnimator();
            fileList.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (fileListLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 0));
                    appBarLayout.setStateListAnimator(stateListAnimator);
                } else {
                    StateListAnimator stateListAnimator1 = new StateListAnimator();
                    stateListAnimator1.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 16));
                    appBarLayout.setStateListAnimator(stateListAnimator1);
                }
            });
            RecyclerView indexedFilesList = findViewById(R.id.indexedFilesList);
            indexedFilesList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            indexedFilesList.setAdapter(new PluginVerticalListAdapter(this, ((FridayApplication) getApplication()).getIndexedFiles()));
        }
    }

    @Override
    public void onBackPressed() {
        FileSystemArrayAdapter fileListAdapter = ((FileSystemArrayAdapter) fileList.getAdapter());
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (fileListAdapter.sourceFile.getParentFile() != null && fileListAdapter.sourceFile.getParentFile().canRead()) {
            Log.d(LOGTAG, "parent:" + fileListAdapter.sourceFile.getParentFile().getPath());
            fileListAdapter.setDirectoryList(fileListAdapter.sourceFile.getParentFile());
        } else {
            super.onBackPressed();
        }
    }

    class FileSystemArrayAdapter extends RecyclerView.Adapter<FileViewHolder> {
        File sourceFile;
        List<File> directoryList;

        FileSystemArrayAdapter(File startFrom) {
            Log.d(LOGTAG, "starting file array adapter");
            setDirectoryList(startFrom);
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FileViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.file_selector_list_item, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            Log.d(LOGTAG, "creating item " + position);
            File directoryFileItem = directoryList.get(position);
            holder.fileName.setText(directoryFileItem.getName());
            if (directoryFileItem.isDirectory() && !directoryFileItem.isFile()) {
                holder.fileIcon.setImageDrawable(getDrawable(R.drawable.ic_twotone_folder_24px));
                holder.fileSize.setVisibility(View.GONE);
                holder.root.setOnClickListener(v -> {
                    setResult(RESULT_CANCELED);
                    Log.d(LOGTAG, directoryFileItem.getPath());
                    if (directoryFileItem.canRead()) setDirectoryList(directoryFileItem);
                });
            } else {
                //TODO: Add visible sign that marks an file as selected
                holder.fileIcon.setImageDrawable(getDrawable(R.drawable.ic_twotone_insert_drive_file_24px));
                holder.root.setOnClickListener(v -> {
                    holder.fileIcon.animate().scaleX(0f).scaleY(0f).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                    holder.iconProgress.animate().scaleX(1f).scaleY(1f).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
                    //Running verification on an file can take some time, so we run it on another thread
                    new Thread(() -> {
                        try {
                            if (FileUtil.getFileExtension(directoryFileItem).equals(".jar") && verify(new JarFile(directoryFileItem))) {
                                Intent resultIntent = new Intent();
                                resultIntent.setData(Uri.fromFile(directoryFileItem));
                                setResult(RESULT_OK, resultIntent);
                                openPlugin.setEnabled(true);
                                openPlugin.setOnClickListener((view) -> finish());
                            } else {
                                setResult(RESULT_CANCELED);
                                openPlugin.setEnabled(false);
                            }
                        } catch (IOException e) {
                            Log.e(LOGTAG, e.getMessage(), e);
                        }
                        runOnUiThread(() -> {
                            holder.fileIcon.animate().scaleX(1f).scaleY(1f).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
                            holder.iconProgress.animate().scaleX(0f).scaleY(0f).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                        });
                    }).start();
                });
            }

        }

        @Override
        public int getItemCount() {
            return sourceFile.listFiles().length;
        }

        public void setDirectoryList(File directoryFile) {
            if (sourceFile != null) {
                notifyItemRangeRemoved(0, sourceFile.listFiles() != null ? sourceFile.listFiles().length : 0);
            }
            sourceFile = directoryFile;
            directoryList = Arrays.asList(directoryFile.listFiles());
            directoryList.sort((file1, file2) -> {
                if (file1.isDirectory() && file2.isFile())
                    return -1;
                if (file1.isDirectory() && file2.isDirectory()) {
                    return 0;
                }
                if (file1.isFile() && file2.isFile()) {
                    return 0;
                }
                return 1;
            });
            notifyItemRangeInserted(0, directoryList.size());
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        ImageView fileIcon;
        ProgressBar iconProgress;
        TextView fileName;
        TextView fileSize;

        FileViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.itemRootLayout);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            iconProgress = itemView.findViewById(R.id.iconProgress);
            fileSize = itemView.findViewById(R.id.fileSize);
            fileName = itemView.findViewById(R.id.fileName);
        }
    }
}
