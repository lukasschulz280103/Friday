package com.friday.ar.preferences.ui.dialog

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.friday.ar.core.Theme
import com.friday.ar.core.activity.FridayActivity.Companion.LOGTAG
import com.friday.ar.preferences.R
import com.friday.ar.preferences.ui.SettingsActivity
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

/**
 * Displays a dialog to the user where he is able to choose one of six themes for friday
 *
 * @see Theme
 */
class ThemeDialog : DialogFragment() {
    private val theme: Theme? = null
    private var hasChanged = false
    private lateinit var mContext: Context
    var listener: OnSelectedTheme? = null
        private set
    private val preferences: SharedPreferences by inject()
    private val doneclick = View.OnClickListener {
        dismiss()
        (activity as SettingsActivity).onSelectedTheme(hasChanged)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = object : OnSelectedTheme {
            override fun onSelectedTheme(hasChanged: Boolean) {
                this@ThemeDialog.hasChanged = hasChanged
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window!!.setWindowAnimations(
                R.style.ThemeSelectDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogview = inflater.inflate(R.layout.theme_select_layout_content, container)
        dialogview.findViewById<View>(R.id.theme_select_cancel).setOnClickListener { dismiss() }
        dialogview.findViewById<View>(R.id.theme_select_done).setOnClickListener(doneclick)
        val themeItemGrid = dialogview.findViewById<GridView>(R.id.theme_dialog_grid)
        themeItemGrid.adapter = ThemeSelectItemAdapter(mContext, R.layout.theme_select_item)
        return dialogview
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    fun setOnApplyThemeListener(listener: OnSelectedTheme) {
        this.listener = listener
    }

    interface OnSelectedTheme {
        fun onSelectedTheme(hasChanged: Boolean)
    }

    private inner class ThemeSelectItemAdapter(internal var context: Context, @LayoutRes res: Int) : ArrayAdapter<Theme>(context, res) {
        internal lateinit var gradientDrawable: GradientDrawable
        internal lateinit var itemView: ImageView
        internal var selectedItem: Int = 0
        private lateinit var gradient: ImageView
        internal var themeClick = View.OnClickListener { view ->
            selectedItem = view.tag as Int
            listener!!.onSelectedTheme(preferences.getInt("theme", get<Theme>().getCurrentAppTheme()) != Theme.themes[selectedItem])
            preferences.edit().putInt("theme", Theme.themes[selectedItem]).apply()
            gradient.setImageDrawable(createGradient(true, view.tag as Int))
            Log.d(LOGTAG, "Clicked theme is already selected.Tag:" + view.tag)
            notifyDataSetChanged()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var itemView = view
            if (itemView == null) {
                itemView = layoutInflater.inflate(R.layout.theme_select_item, viewGroup, false)
            }
            gradient = itemView!!.findViewById(R.id.gradient_bg)
            val th = Theme(context, Theme.themes[i])
            val text = itemView.findViewById<TextView>(R.id.theme_name)
            val card = itemView.findViewById<CardView>(R.id.theme_item_card)
            text.text = th.getNameForPos(i)
            text.setTextColor(th.getTextColorSecondary(i))
            gradientDrawable = createGradient(false, i)
            if (th.styleRes == preferences.getInt("theme", R.style.AppTheme)) {
                gradientDrawable = createGradient(true, i)
                selectedItem = i
            }
            gradient.setImageDrawable(gradientDrawable)
            card.setOnClickListener(themeClick)
            card.tag = i
            return itemView
        }

        override fun getCount(): Int {
            return Theme.colors.size
        }

        private fun createGradient(isSelected: Boolean, pos: Int): GradientDrawable {
            Log.d(LOGTAG, "Createing gradient")
            gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TR_BL, Theme(context).getColorsForPos(pos))
            if (isSelected) {
                gradientDrawable.setStroke(5, ContextCompat.getColor(context, R.color.selection_green))
                gradientDrawable.cornerRadius = 14f
            }
            return gradientDrawable
        }
    }
}
