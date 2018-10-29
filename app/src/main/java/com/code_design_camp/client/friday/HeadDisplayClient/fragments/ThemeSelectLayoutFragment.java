package com.code_design_camp.client.friday.HeadDisplayClient.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ThemeSelectLayoutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentview = inflater.inflate(R.layout.theme_select_layout_content, container, false);
        GridView gv = contentview.findViewById(R.id.theme_dialog_grid);
        gv.setAdapter(new GridAdapter(getContext()));
        return contentview;
    }

    public class GridAdapter extends ArrayAdapter {
        Context mContext;

        GridAdapter(Context c) {
            super(c, R.layout.theme_select_item);
            this.mContext = c;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.theme_select_item, parent, false);
            }
            CircularImageView colorview = convertView.findViewById(R.id.item_color_view);
            Drawable color = getResources().getDrawable(R.drawable.theme_select_item_drawable, null);
            color.setColorFilter(Theme.getColorForPos(mContext, position), PorterDuff.Mode.MULTIPLY);
            colorview.setImageDrawable(color);
            return convertView;
        }

        @Override
        public int getCount() {
            return Theme.colors.length;
        }
    }
}
