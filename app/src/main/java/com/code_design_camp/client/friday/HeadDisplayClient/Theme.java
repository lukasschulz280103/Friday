package com.code_design_camp.client.friday.HeadDisplayClient;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;

import androidx.annotation.StyleRes;

/**
 * Manages the apps Theme resources
 */
public class Theme {
    public static int[] colors = new int[]{
            R.color.colorPrimary,
            R.color.GreenPrimaryColor,
            R.color.BluePrimaryColor,
            R.color.MagentaPrimaryColor,
            R.color.DarkPrimaryColor,
            R.color.RedPrimaryColor,
            R.color.PinkPrimaryColor
    };
    private static int[] themes = new int[]{
            R.style.AppTheme,
            R.style.AppTheme_Custom_Green,
            R.style.AppTheme_Custom_Blue,
            R.style.AppTheme_Custom_Magenta,
            R.style.AppTheme_Custom_Dark,
            R.style.AppTheme_Custom_Red,
            R.style.AppTheme_Custom_Pink
    };
    private static int[] gradientColors = new int[]{
            R.color.colorSecondary,
            R.color.GreenSecondaryColor,
            R.color.BlueSecondaryColor,
            R.color.MagentaSecondaryColor,
            R.color.DarkSecondaryColor,
            R.color.RedSecondaryColor,
            R.color.PinkSecondaryColor
    };
    private static int[] textSecondaryColors = new int[]{
            android.R.color.black,
            R.color.GreenSecondaryTextColor,
            R.color.BlueSecondaryTextColor,
            R.color.MagentaSecondaryTextColor,
            R.color.DarkSecondaryTextColor,
            R.color.RedSecondaryTextColor,
            R.color.PinkSecondaryTextColor
    };
    private static int[] names = new int[]{
            R.string.theme_default,
            R.string.theme_green,
            R.string.theme_blue,
            R.string.theme_magenta,
            R.string.theme_dark,
            R.string.theme_red,
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

    /**
     *
     * @return returns all available app themes
     */
    public static int[] getThemes() {
        return themes;
    }

    /**
     *
     * @param c Context to resolve saved info from
     * @return Returns the current app themes style resource integer
     */
    public static int getCurrentAppTheme(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt("theme", R.style.AppTheme);
    }

    /**
     *
      * @param pos position of theme element
     * @return return gradient colors for the theme at the specified position
     */
    public int[] getColorsForPos(int pos) {
        return new int[]{mContext.getColor(colors[pos]), mContext.getColor(gradientColors[pos])};
    }

    /**
     *
     * @param pos position of theme element
     * @return secondary color of theme
     */
    public int getTextColorSecondary(int pos) {
        return mContext.getColor(textSecondaryColors[pos]);
    }

    /**
     *
     * @param pos position of theme element
     * @return get theme name at position
     */
    public String getNameForPos(int pos) {
        return mContext.getString(names[pos]);
    }

    /**
     *
     * @return get current app themes {@link StyleRes}
     */
    public int getStyleRes() {
        return style;
    }

    /**
     *
     * @param appTheme current {@link StyleRes} int index of the apps theme
     * @return index of the appTheme in {@link Theme} theme arrays. Returns -1 if the given theme cannot be found.
     */
    public int indexOf(int appTheme){
        for(int i = 0;i<themes.length;i++){
            if(themes[i] == appTheme) return i;
        }
        return -1;
    }

    /**
     *
     * @return Returns every themes <b>bottom left</b> gradient color(Gradient is visible in e.g. Settings>Design)
     */
    public static int[] getGradientColors() {
        return gradientColors;
    }

    /**
     *
     * @param pos color index in theme
     * @return gradient of the two coMain menu | File | New | Vector Assetlors at the position
     */
    public GradientDrawable createGradient(int pos){
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, getColorsForPos(pos));
    }
}