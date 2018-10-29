package com.code_design_camp.client.friday.HeadDisplayClient.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;

public class ThemeSelectPreference extends Preference {
    private AlertDialog mThemeDialog;
    private Context mContext;
    private OnSelectedTheme mListener;

    public ThemeSelectPreference(Context c, AttributeSet attrs) {
        super(c, attrs);
        this.mContext = c;
    }

    public void showDialog(OnSelectedTheme listener) {
        this.mListener = listener;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View titleview = inflater.inflate(R.layout.colorful_title, null, false);
        TextView title = titleview.findViewById(R.id.alertTitle);
        ImageButton done = titleview.findViewById(R.id.theme_select_done);
        ImageButton cancel = titleview.findViewById(R.id.theme_select_cancel);
        title.setText(R.string.select_theme);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectedTheme(new Theme());
                mThemeDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mThemeDialog.dismiss();
            }
        });
        //TODO: Fix bug that app crashes when dialog is displayed second time
        AlertDialog.Builder mThemeDialogBuilder = new AlertDialog.Builder(mContext, R.style.ThemeSelectDialog);
        mThemeDialogBuilder.setCustomTitle(titleview);
        mThemeDialogBuilder.setView(R.layout.theme_select_layout);
        this.mThemeDialog = mThemeDialogBuilder.create();
        this.mThemeDialog.show();
    }

    public interface OnSelectedTheme {
        void onSelectedTheme(Theme t);
    }
}
