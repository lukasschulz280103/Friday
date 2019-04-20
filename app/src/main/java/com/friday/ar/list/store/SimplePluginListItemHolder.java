package com.friday.ar.list.store;

import android.view.View;
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

    public SimplePluginListItemHolder(@NonNull View itemView) {
        super(itemView);
        iconView = itemView.findViewById(R.id.icon);
        title = itemView.findViewById(R.id.name);
        installStatus = itemView.findViewById(R.id.installStatus);
        ratingBar = itemView.findViewById(R.id.rating);
        size = itemView.findViewById(R.id.size);
    }
}
