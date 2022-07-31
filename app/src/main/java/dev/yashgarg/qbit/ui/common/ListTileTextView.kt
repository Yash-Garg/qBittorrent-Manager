package dev.yashgarg.qbit.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import dev.yashgarg.qbit.R

class ListTileTextView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var title: TextView
    private var subtitle: TextView

    fun setSubtitle(text: String) {
        subtitle.text = text
    }

    init {
        val typedArr = context.obtainStyledAttributes(attrs, R.styleable.ListTileTextView, 0, 0)
        View.inflate(context, R.layout.list_tile, this)

        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)

        try {
            title.text = typedArr.getString(R.styleable.ListTileTextView_title)
        } finally {
            typedArr.recycle()
        }
    }
}
