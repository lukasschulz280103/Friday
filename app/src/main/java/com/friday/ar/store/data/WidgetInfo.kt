package com.friday.ar.store.data


import android.media.Image
import java.util.*

class WidgetInfo {
    var id: Int = 0
    var title: String? = null
    var authorName: String? = null
    var thumbnailSrc: String? = null
    var extraType: TYPE? = null
    var downloads: String? = null
    var category: String? = null
    var videoUrl: String? = null
    var screenshots: ArrayList<Image>? = null
    var rating: Double = 0.toDouble()
    var price: Double = 0.toDouble()

    enum class TYPE {
        HOT,
        SALE,
        FREE,
        NEW,
        CHOSEN
    }

    class ExtraInfo(private var mData: Map<String, Boolean>) {
        private var hasExtra: Boolean = false
        var isFreeLimited: Boolean = false
            internal set
        var extraType: String? = null
            internal set

        init {
            processData()
        }

        private fun processData() {
            hasExtra = (mData["hasExtra"])!!
            isFreeLimited = (mData["isFreeLimited"])!!
        }

    }
}
