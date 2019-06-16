package com.friday.ar.plugin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.R;

import java.io.File;
import java.util.ArrayList;
import java.util.jar.JarFile;

import static android.app.Activity.RESULT_OK;

//TODO: Add functionality that makes an item selectable
public class PluginVerticalListAdapter extends RecyclerView.Adapter<PluginViewHolder> {
    private ArrayList<JarFile> dataList;
    private Activity context;

    public PluginVerticalListAdapter(@NonNull Activity context, @NonNull ArrayList<JarFile> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public PluginViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PluginViewHolder(LayoutInflater.from(context).inflate(R.layout.file_selector_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PluginViewHolder holder, int position) {
        JarFile file = dataList.get(position);
        holder.title.setText(file.getName().substring(file.getName().lastIndexOf("/")));
        holder.path.setText(file.getName());
        holder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_twotone_insert_drive_file_24px));
        holder.root.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.setData(Uri.fromFile(new File(file.getName())));
            context.setResult(RESULT_OK, resultIntent);
            context.findViewById(R.id.select_file).setEnabled(true);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

