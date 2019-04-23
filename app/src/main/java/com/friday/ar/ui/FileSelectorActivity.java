package com.friday.ar.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.activities.FridayActivity;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

//TODO:Add support for opening directories
//TODO:Add support for selecting files
public class FileSelectorActivity extends FridayActivity {
    public static final String LOGTAG = "FileSelector";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 8001);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_file_selector);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.open_plugin_file);
        //TODO:Fix NullPointerException when returning result to calling activity
        setResult(RESULT_CANCELED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 8001 && Arrays.equals(grantResults, new int[]{PERMISSION_GRANTED})) {
            RecyclerView fileList = findViewById(R.id.fileList);
            fileList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            fileList.setAdapter(new FileSystemArrayAdapter(new File("/")));
        }
    }

    class FileSystemArrayAdapter extends RecyclerView.Adapter<FileViewHolder> {
        File sourceFile;
        List<File> directoryList;

        FileSystemArrayAdapter(File startFrom) {
            this.sourceFile = startFrom;
            File[] directoryArray = startFrom.listFiles();
            directoryList = Arrays.asList(directoryArray);
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
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FileViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.file_selector_list_item, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            File directoryFileItem = directoryList.get(position);
            holder.fileName.setText(directoryFileItem.getName());
            if (directoryFileItem.isDirectory() && !directoryFileItem.isFile()) {
                holder.fileIcon.setImageDrawable(getDrawable(R.drawable.ic_twotone_folder_24px));
                holder.folderInfos.setVisibility(View.VISIBLE);
                holder.fileSize.setVisibility(View.GONE);
                Log.d(LOGTAG, "directoryFileItem folders = " + directoryFileItem.listFiles());
                //TODO:Fix bug that directory preview is tried to be shown on files
                holder.containingFolders.setText(Integer.toString(directoryFileItem.listFiles().length));
                holder.containingFiles.setText(Integer.toString(directoryFileItem.listFiles(File::isFile).length));
            } else {
                holder.fileIcon.setImageDrawable(getDrawable(R.drawable.ic_twotone_insert_drive_file_24px));
            }

        }

        @Override
        public int getItemCount() {
            return sourceFile.listFiles().length;
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        ImageView fileIcon;
        TextView fileName;
        TextView fileSize;
        LinearLayout folderInfos;
        TextView containingFolders;
        TextView containingFiles;

        FileViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.itemRootLayout);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileSize = itemView.findViewById(R.id.fileSize);
            fileName = itemView.findViewById(R.id.fileName);
            folderInfos = itemView.findViewById(R.id.folderInfos);
            containingFolders = itemView.findViewById(R.id.folderContainingFolders);
            containingFiles = itemView.findViewById(R.id.folderContainingFiles);
        }
    }
}
