package org.phantancy.fill_blank_text

import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.text.*
import android.text.method.LinkMovementMethod
import android.util.ArrayMap
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import org.phantancy.fill_blank_text.entity.BlankEntity
import org.phantancy.fill_blank_text.span.AbstractSpan
import org.phantancy.fill_blank_text.span.RectSpan
import org.phantancy.fill_blank_text.span.StarSpan
import org.xml.sax.XMLReader

class SpanController {
    val EDITTEXT_TAG_NAME = "EditText"
    val TEXTVIEW_TAG_NAME = "TextView"
    val STAR_TAG_NAME = "Star"
    var tvContent: TextView? = null
    var etInput: EditText? = null
    private var mSpans = arrayListOf<AbstractSpan>()

    private var mActy: Activity? = null
    private var checkedSpan: RectSpan? = null
    var mOldSpanId = ""
    var idMap: ArrayMap<String, Int> = ArrayMap()

    constructor(mActy: Activity?, tvContent: TextView?, etInput: EditText?) {
        this.tvContent = tvContent
        this.etInput = etInput
        this.mActy = mActy
    }

    fun fillBlank(content: String, defaultValues: List<BlankEntity>) {
        tvContent?.movementMethod = MyMethod()
        val spanned = Html.fromHtml(
            content,
            null,
            object : Html.TagHandler {
                var index = 0;
                override fun handleTag(
                    opening: Boolean,
                    tag: String?,
                    output: Editable?,
                    xmlReader: XMLReader?
                ) {

                    if (tag.equals(EDITTEXT_TAG_NAME, ignoreCase = true) && opening) {
                        if (tvContent != null) {
                            var paint = TextPaint(tvContent?.paint)
                            val entity = defaultValues[index]
                            val span = tvContent!!.context.let { RectSpan(it, paint, entity.blankWidth, entity.blankHeight) }
                            if (mActy is AbstractSpan.OnClickListener) {
                                span?.mOnClick = mActy as AbstractSpan.OnClickListener
                            }
                            span?.mText = defaultValues[index].defaultValue
                            span.id = entity.id
                            idMap.put(entity.id,index)
                            mSpans.add(span)
                            index++
                            output?.setSpan(
                                span,
                                output.length - 1,
                                output.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                    if (tag.equals(TEXTVIEW_TAG_NAME, ignoreCase = true) && opening) {
                        if (tvContent != null) {
                            var paint = TextPaint(tvContent?.paint)
                            val entity = defaultValues[index]
                            val span = RectSpan(tvContent!!.context, paint, entity.blankWidth, entity.blankHeight)
                            if (mActy is AbstractSpan.OnClickListener) {
                                span?.mOnClick = mActy as AbstractSpan.OnClickListener
                            }
                            span?.mText = defaultValues[index].defaultValue
                            span.id = entity.id
                            idMap.put(entity.id,index)
                            mSpans.add(span)
                            index++
                            output?.setSpan(
                                span,
                                output.length - 1,
                                output.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                    if (tag.equals(STAR_TAG_NAME, ignoreCase = true) && opening) {
                        if (tvContent != null) {
                            var paint = TextPaint(tvContent?.paint)
                            val entity = defaultValues[index]
                            val span = StarSpan(tvContent!!.context,paint,entity.blankWidth)
                            span.id = entity.id
                            idMap.put(entity.id,index)
                            mSpans.add(span)
                            index++
                            output?.setSpan(
                                    span,
                                    output.length - 1,
                                    output.length,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }

                }
            })
        tvContent?.setText(spanned)
    }

    fun setSpanChecked(id: String){
        val position = idMap.get(id) ?: return
        if (mSpans[position] is RectSpan){
            checkedSpan = mSpans[position] as RectSpan
            tvContent?.invalidate()
        }


    }
    var mRf: RectF? = null
    var mFontTop :Int = 0
    var mFontBottom: Int = 0
    //填充缓存的数据
    fun setData(str: String, id: String) {
        val pos = idMap.get(id) ?: return
        if (tvContent == null || mSpans == null || mSpans.size == 0 || pos < 0 || pos > mSpans.size - 1) return
        if (mSpans[pos] is RectSpan) {
            (mSpans[pos] as RectSpan).mText = str
            tvContent?.invalidate()
        }
    }

    fun getSpanRect(span: RectSpan): RectF {
        val layout = tvContent?.layout
        val buffer = tvContent?.text as Spannable
        var l = buffer.getSpanStart(span)
        var r = buffer.getSpanEnd(span)
        var line = layout?.getLineForOffset(l)
        var line2  = layout?.getLineForOffset(r)
        if (mRf == null){
            mRf = RectF()
            val fontMetrics = tvContent?.paint?.fontMetrics
            mFontTop = (fontMetrics?.ascent)?.toInt()!!
            mFontBottom = (fontMetrics?.descent)?.toInt()!!
        }
        mRf?.left = layout?.getPrimaryHorizontal(l)
        mRf?.right = layout?.getSecondaryHorizontal(r)
        line = line?.let { layout?.getLineBaseline(it) }
        if (line != null) {
            mRf?.top = (line + mFontTop).toFloat()
            mRf?.bottom = (line + mFontBottom).toFloat()
        }
        return mRf as RectF
    }

    //设置Edittext位置
    fun setEditTextRectF(rf: RectF){
        if (etInput != null && tvContent != null) {
            val lp = etInput!!.layoutParams as FrameLayout.LayoutParams
            lp.width = (rf.right - rf.left).toInt()
            lp.height = (rf.bottom - rf.top).toInt()
            lp.leftMargin = (tvContent!!.left + rf.left).toInt()
            lp.topMargin = (tvContent!!.top + rf.top).toInt()
            etInput!!.layoutParams = lp
            etInput!!.requestFocus()
            showImm(true, etInput)
        }
    }

    fun showImm(isShow: Boolean, focus: View?) {
        try {
            if (isShow) {
                if (focus != null) {
                    show(true, focus)
                }
            } else {
                if (focus != null) {
                    show(false, focus)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(isShow: Boolean, focus: View): Boolean {
        val imm = focus.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return if (isShow) {
            focus.requestFocus()
            imm.showSoftInput(focus, 0)
        } else {
            imm.hideSoftInputFromWindow(focus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
    fun getResult():List<String>{
        val list = ArrayList<String>()
        if (mSpans != null) {
            for (span in mSpans){
                if (span is RectSpan) {
                    list.add(span.mText)
                }
            }
        }
        return list
    }

    fun setLastCheckedSpanText(editText: String) {
        if (checkedSpan != null) {
            checkedSpan!!.mText = editText
            tvContent?.invalidate()
            etInput?.visibility = View.GONE
        }
    }
}

class MyMethod : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView?, buffer: Spannable?, event: MotionEvent?): Boolean {
        val action = event!!.action

        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN
        ) {
            var x = event!!.x.toInt()
            var y = event!!.y.toInt()

            x -= widget!!.totalPaddingLeft
            y -= widget!!.totalPaddingTop

            x += widget!!.scrollX
            y += widget!!.scrollY

            val layout = widget!!.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val link: Array<RectSpan> = buffer!!.getSpans(off, off, RectSpan::class.java)
            if (link.size != 0) {
                if (action == MotionEvent.ACTION_DOWN) {
                    link[0].onClick(widget, buffer, true, x, y, line, off)
                    return true
                }
            }

        }
        return false
    }


}
