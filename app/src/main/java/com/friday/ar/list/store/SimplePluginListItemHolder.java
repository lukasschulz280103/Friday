package com.friday.ar.list.store;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.R;

public class SimplePluginListItemHolder extends RecyclerView.ViewHolder {
    public ImageView iconView;
    public TextView title;
    public TextView installStatus;
    public RatingBar ratingBar;
    public TextView size;
    public ImageButton overflowMenu;

    public SimplePluginListItemHolder(@NonNull View itemView) {
        super(itemView);
        iconView = itemView.findViewById(R.id.fileIcon);
        title = itemView.findViewById(R.id.name);
        installStatus = itemView.findViewById(R.id.installStatus);
        ratingBar = itemView.findViewById(R.id.rating);
        size = itemView.findViewById(R.id.size);
        overflowMenu = itemView.findViewById(R.id.overflowMenu);
    }
}
