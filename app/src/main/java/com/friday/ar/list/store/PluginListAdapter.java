package com.friday.ar.list.store;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.R;
import com.friday.ar.plugin.Plugin;

import java.util.List;

public class PluginListAdapter extends RecyclerView.Adapter<SimplePluginListItemHolder> {
    private static final String LOGTAG = "PluginListAdapter";
    private Context context;
    private List<Plugin> dataList;

    public PluginListAdapter(Context context, @NonNull List<Plugin> data) {
        this.context = context;
        this.dataList = data;
    }

    @NonNull
    @Override
    public SimplePluginListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.listitem_app_item, parent, false);
        return new SimplePluginListItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimplePluginListItemHolder holder, int position) {
        Plugin plugin = dataList.get(position);
        holder.iconView.setImageURI(plugin.getIconURI());
        holder.title.setText(plugin.getName());
        if (plugin.getRating() != null) {
            holder.ratingBar.setRating(plugin.getRating().getStarRating());
        } else {
            holder.ratingBar.setVisibility(View.GONE);
        }
        holder.installStatus.setText(R.string.store_plugin_status_installed);
        holder.overflowMenu.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(context, v, Gravity.START);
            menu.inflate(R.menu.store_plugin_item_more);
            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.uninstall_plugin: {
                        Log.d(LOGTAG, "Uninstalling " + plugin.getPluginFile().getName());
                        if (!plugin.getPluginFile().delete()) {
                            plugin.getPluginFile().deleteOnExit();
                        }
                        break;
                    }
                }
                return true;
            });
            menu.show();
        });
        holder.size.setText(Long.toString(plugin.getPluginFile().length()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
