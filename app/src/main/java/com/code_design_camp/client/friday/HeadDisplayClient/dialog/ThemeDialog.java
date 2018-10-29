package com.code_design_camp.client.friday.HeadDisplayClient.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class ThemeDialog extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment {
    LayoutInflater inflater;
    private Context context;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    View.OnClickListener doneclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            context.setTheme(android.R.style.Theme_Material_Light_DarkActionBar);
            dialog.dismiss();
        }
    };

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View titleview = inflater.inflate(R.layout.colorful_title, null, false);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
        this.inflater = getActivity().getLayoutInflater();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(context, R.style.Base_Theme_MaterialComponents_Dialog_CustomAnimated);
        return builder.create();
    }

    @Override
    public Preference findPreference(CharSequence key) {
        return getPreference();
    }

    private class ThemeSelectItemAdapter extends ArrayAdapter<Theme> {
        Context context;

        public ThemeSelectItemAdapter(Context context, @LayoutRes int res) {
            super(context, res);
            this.context = context;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            CircularImageView image = view.findViewById(R.id.item_color_view);
            image.setColorFilter(Theme.getColorForPos(context, i), android.graphics.PorterDuff.Mode.MULTIPLY);
            return view;
        }

        @Override
        public int getCount() {
            return 7;
        }
    }
}
