package com.friday.ar.core

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.annotation.StyleRes
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * Manages the apps Theme resources
 */
class Theme : KoinComponent {
    companion object {
        var colors = intArrayOf(R.color.colorPrimary, R.color.GreenPrimaryColor, R.color.BluePrimaryColor, R.color.MagentaPrimaryColor, R.color.DarkPrimaryColor, R.color.RedPrimaryColor, R.color.PinkPrimaryColor)
        /**
         * @return returns all available app themes
         */
        val themes = intArrayOf(R.style.AppTheme, R.style.AppTheme_Custom_Green, R.style.AppTheme_Custom_Blue, R.style.AppTheme_Custom_Magenta, R.style.AppTheme_Custom_Dark, R.style.AppTheme_Custom_Red, R.style.AppTheme_Custom_Pink)
        /**
         * @return Returns every themes **bottom left** gradient color(Gradient is visible in e.g. Settings>Design)
         */
        val gradientColors = intArrayOf(R.color.colorSecondary, R.color.GreenSecondaryColor, R.color.BlueSecondaryColor, R.color.MagentaSecondaryColor, R.color.DarkSecondaryColor, R.color.RedSecondaryColor, R.color.PinkSecondaryColor)
        private val textSecondaryColors = intArrayOf(android.R.color.black, R.color.GreenSecondaryTextColor, R.color.BlueSecondaryTextColor, R.color.MagentaSecondaryTextColor, R.color.DarkSecondaryTextColor, R.color.RedSecondaryTextColor, R.color.PinkSecondaryTextColor)
        private val names = intArrayOf(R.string.theme_default, R.string.theme_green, R.string.theme_blue, R.string.theme_magenta, R.string.theme_dark, R.string.theme_red, R.string.theme_pink)

        /**
         * @param c Context to resolve saved info from
         * @return Returns the current app themes style resource integer
         */
    }

    private var mContext: Context
    private val preferences: SharedPreferences = get()


    fun getCurrentAppTheme(): Int {
        return preferences.getInt("theme", R.style.AppTheme)
    }

    /**
     * @return get current app themes [StyleRes]
     */
    @StyleRes
    var styleRes: Int = 0

    constructor(c: Context) {
        mContext = c
    }

    constructor(c: Context, @StyleRes themeRes: Int) {
        mContext = c
        this@Theme.styleRes = themeRes
    }

    /**
     * @param pos position of theme element
     * @return return gradient colors for the theme at the specified position
     */
    fun getColorsForPos(pos: Int): IntArray {
        return intArrayOf(mContext.getColor(colors[pos]), mContext.getColor(gradientColors[pos]))
    }

    /**
     * @param pos position of theme element
     * @return secondary color of theme
     */
    fun getTextColorSecondary(pos: Int): Int {
        return mContext.getColor(textSecondaryColors[pos])
    }

    /**
     * @param pos position of theme element
     * @return get theme name at position
     */
    fun getNameForPos(pos: Int): String {
        return mContext.getString(names[pos])
    }

    /**
     * @param appTheme current [StyleRes] int index of the apps theme
     * @return index of the appTheme in [Theme] theme arrays. Returns -1 if the given theme cannot be found.
     */
    fun indexOf(appTheme: Int): Int {
        for (i in themes.indices) {
            if (themes[i] == appTheme) return i
        }
        return -1
    }

    /**
     * @return gradient of the two colors at the position
     */
    fun createAppThemeGadient(): Drawable {
        return mContext.getDrawable(R.drawable.app_colors_gradient)!!
    }
}