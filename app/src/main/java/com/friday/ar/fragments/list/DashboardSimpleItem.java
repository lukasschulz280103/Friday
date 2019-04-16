package com.friday.ar.fragments.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.friday.ar.R;
import com.friday.ar.ui.StoreDetailActivity;

public class DashboardSimpleItem extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dashboard_item_simple, container, false);
        TextView title = v.findViewById(R.id.title);
        Button toStore = v.findViewById(R.id.morebtn);
        title.setText(R.string.recommendation_picked_widgets_title);
        toStore.setText(R.string.to_store);
        toStore.setOnClickListener(view -> startActivity(new Intent(getActivity(), StoreDetailActivity.class)));
        return v;
    }
}
