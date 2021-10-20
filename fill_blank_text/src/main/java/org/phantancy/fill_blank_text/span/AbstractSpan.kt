package org.phantancy.fill_blank_text.span

import android.text.style.ReplacementSpan
import android.widget.TextView

abstract class AbstractSpan:ReplacementSpan() {
    var mOnClick: OnClickListener? = null
    interface OnClickListener {
        fun OnClick(v: TextView, id: String, span: RectSpan)
    }
}