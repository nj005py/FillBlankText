package org.phantancy.fill_blank_text.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class StarSpan(val context: Context, val mPaint:Paint, var size:Int):ReplacementSpan() {
    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

}