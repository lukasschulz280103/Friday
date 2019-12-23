package com.friday.ar.store.ui.fragments.feed.gridFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.store.R
import com.friday.ar.store.data.glide.GlideApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.feed_item_simple_grid.*


class SimpleGridFragment(val title: String, val message: String, val backgroundImage: String, val pluginList: List<Plugin>?) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_item_simple_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        header.text = title
        info.text = message

        val imageFile = FirebaseStorage.getInstance().getReference("/store/$backgroundImage")

        GlideApp.with(this)
                .load(imageFile)
                .into(image)
    }
}