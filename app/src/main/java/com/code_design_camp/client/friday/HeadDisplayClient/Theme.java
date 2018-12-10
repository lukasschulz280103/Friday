package com.code_design_camp.client.friday.HeadDisplayClient;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.StyleRes;

/*
 * (C) Copyright 2018 Lukas Faber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Lukas Faber
 */

public class Theme {
    public static int[] colors = new int[]{
            R.color.colorPrimary,
            R.color.GreenPrimaryColor,
            R.color.BluePrimaryColor,
            R.color.MagentaPrimaryColor,
            R.color.DarkPrimaryColor,
            R.color.PinkPrimaryColor
    };
    private static int[] themes = new int[]{
            R.style.AppTheme,
            R.style.AppTheme_Custom_Green,
            R.style.AppTheme_Custom_Blue,
            R.style.AppTheme_Custom_Magenta,
            R.style.AppTheme_Custom_Dark,
            R.style.AppTheme_Custom_Pink
    };
    private static int[] gradientColors = new int[]{
            R.color.colorSecondary,
            R.color.GreenSecondaryColor,
            R.color.BlueSecondaryColor,
            R.color.MagentaSecondaryColor,
            R.color.DarkSecondaryColor,
            R.color.PinkSecondaryColor
    };
    private static int[] textSecondaryColors = new int[]{
            android.R.color.black,
            R.color.GreenSecondaryTextColor,
            R.color.BlueSecondaryTextColor,
            R.color.MagentaSecondaryTextColor,
            R.color.DarkSecondaryTextColor,
            R.color.PinkSecondaryTextColor
    };
    private static int[] names = new int[]{
            R.string.theme_default,
            R.string.theme_green,
            R.string.theme_blue,
            R.string.theme_magenta,
            R.string.theme_dark,
            R.string.theme_pink
    };
    private Context mContext;
    @StyleRes
    private int style;

    public Theme(Context c) {
        mContext = c;
    }

    public Theme(Context c, @StyleRes int themeRes) {
        mContext = c;
        style = themeRes;
    }

    public static int[] getThemes() {
        return themes;
    }

    public static int getCurrentAppTheme(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt("theme", R.style.AppTheme);
    }

    public int[] getColorsForPos(int pos) {
        return new int[]{mContext.getResources().getColor(colors[pos]), mContext.getResources().getColor(gradientColors[pos])};
    }

    public int getTextColorSecondary(int pos) {
        return mContext.getResources().getColor(textSecondaryColors[pos]);
    }

    public String getNameForPos(int pos) {
        return mContext.getString(names[pos]);
    }

    public int getStyleRes() {
        return style;
    }
}