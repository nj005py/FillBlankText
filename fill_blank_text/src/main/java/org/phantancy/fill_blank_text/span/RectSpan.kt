package org.phantancy.fill_blank_text.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Spannable
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ReplacementSpan
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat

class RectSpan(val context: Context, val mPaint: Paint,var textWidth:Int,var textHeight:Int) : AbstractSpan() {
    var mText = "" //保存的String
    var mObject: Any? = null
    var id = "" //回调中的对应Span的ID
//    var mOnClick:OnClickListener? = null
    init {
        textWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            textWidth.toFloat(), context.resources.displayMetrics
        ).toInt()
        textHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            textHeight.toFloat(), context.resources.displayMetrics
        ).toInt()
    }

    fun setDrawTextColor(res: Int) = mPaint.setColor(ContextCompat.getColor(context, res))
    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return textWidth
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
        val ellipsize = TextUtils.ellipsize(
            mText,
            paint as TextPaint,
            textWidth.toFloat(),
            TextUtils.TruncateAt.END
        )
        var width = paint.measureText(ellipsize, 0, ellipsize.length).toInt()
        width = (textWidth - width) / 2
        canvas.drawText(ellipsize, 0, ellipsize.length, x + width, y.toFloat(), mPaint)
        val strokePaint = Paint()
        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = Color.parseColor("#B5B5B5")
        strokePaint.strokeWidth = 2f
        val rect = Rect(x.toInt() + 2, top + 2, x.toInt() + textWidth, bottom - 2)
        canvas.drawRect(rect, strokePaint)
    }

    fun onClick(
        v: TextView,
        buffer: Spannable,
        isDown: Boolean,
        x: Int,
        y: Int,
        line: Int,
        off: Int
    ) {
        mOnClick?.OnClick(v, id, this)
    }

//    interface OnClickListener {
//        fun OnClick(v: TextView, id: Int, span: RectSpan)
//    }

}