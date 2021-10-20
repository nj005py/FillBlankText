package org.phantancy.fill_blank_text.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.ReplacementSpan
import android.util.TypedValue

class StarSpan(val context: Context, val mPaint: Paint, var size: Int) : AbstractSpan() {
    var id = "" //回调中的对应Span的ID

    init {
        size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size.toFloat(), context.resources.displayMetrics).toInt()
    }

    override fun getSize(
            paint: Paint,
            text: CharSequence?,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt?
    ): Int {
        return size
    }

    override fun draw(
            canvas: Canvas,
            text: CharSequence?,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
    ) {
//        val mPaint = Paint().apply {
//            color = Color.RED
//            textSize = 60f
//        }
        mPaint.apply {
            color = Color.RED
        }
        var width = paint.measureText("*", 0, 1).toInt()
        width = (size - width) / 2
        var height = (bottom - top) / 2
        canvas.drawText("*", 0, 1, x + width, bottom.toFloat(), mPaint)
    }

}