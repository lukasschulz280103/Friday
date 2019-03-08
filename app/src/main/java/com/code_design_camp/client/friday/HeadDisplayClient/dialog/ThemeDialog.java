package com.code_design_camp.client.friday.HeadDisplayClient.dialog;

import android.content.Context;
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

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;
import com.code_design_camp.client.friday.HeadDisplayClient.ui.SettingsActivity;

/**
 * Displays a dialog to the user where he is able to choose one of six themes for friday
 * @see Theme
 */
public class ThemeDialog extends DialogFragment {
    private Theme theme;
    private boolean hasChanged = false;
    private Context context;
    private OnSelectedTheme mListener;
    private SharedPreferences preferences;
    private View.OnClickListener doneclick = view -> {
        dismiss();
        ((SettingsActivity) getActivity()).onSelectedTheme(hasChanged);
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = (hasChanged) -> this.hasChanged = hasChanged;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public OnSelectedTheme getListener() {
        return mListener;
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
        void onSelectedTheme(boolean hasChanged);
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
            getListener().onSelectedTheme(preferences.getInt("theme", Theme.getCurrentAppTheme(getContext())) != Theme.getThemes()[selectedItem]);
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
