package com.friday.ar.list.store;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.R;
import com.friday.ar.plugin.Plugin;

import java.util.List;

public class PluginListAdapter extends RecyclerView.Adapter<SimplePluginListItemHolder> {
    private Context context;
    private List<Plugin> dataList;

    public PluginListAdapter(Context context, @NonNull List<Plugin> data) {
        this.context = context;
        this.dataList = data;
    }

    @NonNull
    @Override
    public SimplePluginListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.listitem_app_item, parent);
        return new SimplePluginListItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimplePluginListItemHolder holder, int position) {
        Plugin plugin = dataList.get(position);
        holder.iconView.setImageURI(plugin.getIconURI());
        holder.title.setText(plugin.getName());
        holder.ratingBar.setRating(plugin.getRating().getStarRating());
        holder.size.setText(Long.toString(plugin.getPluginFile().length()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
