package dev.yashgarg.qbit.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import dev.yashgarg.qbit.R

class ListTileTextView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var titleTv: TextView
    private var subtitleTv: TextView

    var subtitle: String? = null
        set(value) {
            subtitleTv.text = value
            field = value
        }

    init {
        val typedArr = context.obtainStyledAttributes(attrs, R.styleable.ListTileTextView, 0, 0)
        View.inflate(context, R.layout.list_tile, this)

        titleTv = findViewById(R.id.title)
        subtitleTv = findViewById(R.id.subtitle)

        try {
            titleTv.text = typedArr.getString(R.styleable.ListTileTextView_title)
            subtitleTv.text = subtitle
        } finally {
            typedArr.recycle()
        }
    }
}
