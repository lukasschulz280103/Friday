package com.friday.ar.plugin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.R;

public class PluginViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView path;
    ImageView icon;

    public PluginViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.fileName);
        path = itemView.findViewById(R.id.fileSize);
        icon = itemView.findViewById(R.id.fileIcon);
    }
}
