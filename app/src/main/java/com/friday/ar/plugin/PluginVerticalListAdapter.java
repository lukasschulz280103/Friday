package com.friday.ar.plugin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.R;

import java.util.ArrayList;
import java.util.jar.JarFile;

public class PluginVerticalListAdapter extends RecyclerView.Adapter<PluginViewHolder> {
    private ArrayList<JarFile> dataList;
    private Context context;

    public PluginVerticalListAdapter(@NonNull Context context, @NonNull ArrayList<JarFile> dataList) {
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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

