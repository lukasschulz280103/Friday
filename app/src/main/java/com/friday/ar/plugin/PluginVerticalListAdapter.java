package com.friday.ar.plugin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.R;
import com.friday.ar.plugin.file.ZippedPluginFile;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

//TODO: Add functionality that makes an item selectable
public class PluginVerticalListAdapter extends RecyclerView.Adapter<PluginViewHolder> {
    private ArrayList<ZippedPluginFile> dataList;
    private Activity context;

    public PluginVerticalListAdapter(@NonNull Activity context, @NonNull ArrayList<ZippedPluginFile> dataList) {
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
        ZippedPluginFile file = dataList.get(position);
        holder.title.setText(file.getFile().getName());
        holder.path.setText(file.getFile().getName());
        holder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_twotone_insert_drive_file_24px));
        holder.root.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.setData(Uri.fromFile(new File(file.getFile().getName())));
            context.setResult(RESULT_OK, resultIntent);
            context.findViewById(R.id.select_file).setEnabled(true);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

