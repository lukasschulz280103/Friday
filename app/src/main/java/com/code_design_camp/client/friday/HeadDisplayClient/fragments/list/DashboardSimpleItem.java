package com.code_design_camp.client.friday.HeadDisplayClient.fragments.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.StoreDetailActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
