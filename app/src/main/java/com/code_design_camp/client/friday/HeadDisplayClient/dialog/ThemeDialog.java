package com.code_design_camp.client.friday.HeadDisplayClient.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class ThemeDialog extends DialogFragment {
    private Context context;
    private OnSelectedTheme mListener;
    private SharedPreferences preferences;
    private View.OnClickListener doneclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            getActivity().recreate();
            Intent result = new Intent();
            result.putExtra("themechange", true);
            getActivity().setResult(Activity.RESULT_OK, result);
            mListener.onSelectedTheme(new Theme(context));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setWindowAnimations(
                R.style.ThemeSelectDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogview = inflater.inflate(R.layout.theme_select_layout_content, container);
        dialogview.findViewById(R.id.theme_select_cancel).setOnClickListener((v) -> dismiss());
        dialogview.findViewById(R.id.theme_select_done).setOnClickListener(doneclick);
        GridView themeItemGrid = dialogview.findViewById(R.id.theme_dialog_grid);
        themeItemGrid.setAdapter(new ThemeSelectItemAdapter(context, R.layout.theme_select_item));
        return dialogview;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    public void setOnApplyThemeListener(OnSelectedTheme listener) {
        this.mListener = listener;
    }

    public interface OnSelectedTheme {
        void onSelectedTheme(Theme t);
    }

    private class ThemeSelectItemAdapter extends ArrayAdapter<Theme> {
        private static final String LOGTAG = "ThemeSelectAdapter";
        Context context;
        GradientDrawable gd;
        ImageView itemview;
        int selectedItem;
        View.OnClickListener themeClick = view -> {
            ImageView gradient = view.findViewById(R.id.gradient_bg);
            selectedItem = (int) view.getTag();
            preferences.edit().putInt("theme", Theme.getThemes()[selectedItem]).apply();
            gradient.setImageDrawable(createGradient(true, (int) view.getTag()));
            Log.d(LOGTAG, "Clicked theme is already selected.Tag:" + view.getTag());
            notifyDataSetChanged();
        };

        private ThemeSelectItemAdapter(Context context, @LayoutRes int res) {
            super(context, res);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.theme_select_item, viewGroup, false);
            }
            Theme th = new Theme(context, Theme.getThemes()[i]);
            TextView text = view.findViewById(R.id.theme_name);
            CardView card = view.findViewById(R.id.theme_item_card);
            text.setText(th.getNameForPos(i));
            text.setTextColor(th.getTextColorSecondary(i));
            itemview = view.findViewById(R.id.gradient_bg);
            gd = createGradient(false, i);
            if (th.getStyleRes() == preferences.getInt("theme", R.style.AppTheme)) {
                gd = createGradient(true, i);
                selectedItem = i;
            }
            itemview.setImageDrawable(gd);
            card.setOnClickListener(themeClick);
            card.setTag(i);
            return view;
        }

        @Override
        public int getCount() {
            return Theme.colors.length;
        }

        private GradientDrawable createGradient(boolean isSelected, int pos) {
            Log.d(LOGTAG, "Createing gradient");
            gd = new GradientDrawable(GradientDrawable.Orientation.TR_BL, new Theme(context).getColorsForPos(pos));
            if (isSelected) {
                gd.setStroke(5, ContextCompat.getColor(context, R.color.selection_green));
                gd.setCornerRadius(14);
            }
            return gd;
        }
    }
}
