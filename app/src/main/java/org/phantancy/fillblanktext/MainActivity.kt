package org.phantancy.fillblanktext

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import org.phantancy.fill_blank_text.SpanController
import org.phantancy.fill_blank_text.entity.BlankEntity
import org.phantancy.fill_blank_text.span.AbstractSpan
import org.phantancy.fill_blank_text.span.RectSpan
import org.phantancy.fillblanktext.databinding.ActivityMainBinding
import org.phantancy.fillblanktext.entity.ContractVO

class MainActivity : AppCompatActivity(), AbstractSpan.OnClickListener {
    lateinit var binding: ActivityMainBinding
    lateinit var mSpanController: SpanController
    lateinit var mSpanController2: SpanController
    lateinit var mSpanController3: SpanController
    lateinit var mSpanController4: SpanController
    var vo: ContractVO = ContractVO()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //1
        var blankList = arrayListOf<BlankEntity>(
                BlankEntity("star", "<Star>", "", 10, 0),
                BlankEntity("et_one", "<EditText>", "", 100, 40),
                BlankEntity("et_two", "<EditText>", "", 100, 30),
                BlankEntity("tv_one", "<TextView>", "长长长长长长长长文本a", 200, 30)
        )
        val content = "*${blankList[0].tag}1、文文文文文文文文文文文文文文文文文文文文文文文文文文文文文输入框1.1 ${blankList[1].tag}" +
                "文文文文文文文文文文文文文文文文文文文文文文文文文文文输入框1.2 ${blankList[2].tag}文文文文文文文点击弹窗1.3 ${blankList[3].tag}"
        mSpanController = SpanController(this, binding.tvSpan, binding.etSpan)
        mSpanController.fillBlank(content, blankList)
        //2
        var blankList2 = arrayListOf<BlankEntity>(
                BlankEntity("star", "<Star>", "", 10, 0),
                BlankEntity("et_one", "<EditText>", "", 120, 30),
        )
        val content2 = "*${blankList2[0].tag}2、文文文文文文文文文文文文文文文输入框2.1 ${blankList[1].tag}文文文文文文文文文文文文文文文文文文文文文文文文文文文文"
        mSpanController2 = SpanController(this, binding.tvSpan2, binding.etSpan2)
        mSpanController2.fillBlank(content2, blankList2)
        //3
        var blankList3 = arrayListOf<BlankEntity>(
                BlankEntity("star", "<Star>", "", 10, 0),
                BlankEntity("tv_one", "<TextView>", "文本a", 120, 30),
        )
        val content3 = "*${blankList3[0].tag}3、文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文点击弹窗3.1 ${blankList[1].tag}文"
        mSpanController3 = SpanController(this, binding.tvSpan3, binding.etSpan2)
        mSpanController3.fillBlank(content3, blankList3)

        //输入4
        var blankList4 = arrayListOf<BlankEntity>(
                BlankEntity("et_one", "<EditText>", "", 120, 30),
        )
        val content4 = "4.1文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文文${blankList4[0].tag}文文文文文文"
        mSpanController4 = SpanController(this, binding.tvSpan4, binding.etSpan4)
        mSpanController4.fillBlank(content4, blankList4)
        //保存
        binding.btnSave.setOnClickListener { v ->
            checkInfo()
        }
    }

    override fun OnClick(v: TextView, id: String, span: RectSpan) {
        if (v.id == R.id.tv_span) {
            if (id.equals("tv_one")) {
                binding.etSpan.visibility = View.GONE
                hideInputMethod()
                mSpanController.setData(
                        binding.etSpan.getText().toString(), mSpanController.mOldSpanId
                )
                showPicker(span, arrayListOf("长长长长长长长长文本a", "长长长长长长长长文本b"), id, mSpanController)
            } else {
                binding.etSpan.visibility = View.VISIBLE
                //设置选中的id
                mSpanController.setSpanChecked(id)
                mSpanController.setData(
                        binding.etSpan.getText().toString(), mSpanController.mOldSpanId
                )
                mSpanController.mOldSpanId = id
                //如果当前span身上有值，先赋值给et身上
                binding.etSpan.setText(
                        if (TextUtils.isEmpty(span.mText)) "" else span.mText
                )
                binding.etSpan.setSelection(binding.etSpan.getText().length)
                span.mText = ""
                //通过rf计算出et当前应该显示的位置
                val rf: RectF = mSpanController.getSpanRect(span)
                //设置EditText填空题中的相对位置
                mSpanController.setEditTextRectF(rf)
            }
        } else if (v.id == R.id.tv_span2) {
            binding.etSpan2.visibility = View.VISIBLE
            //设置选中的id
            mSpanController2.setSpanChecked(id)
            mSpanController2.setData(
                    binding.etSpan.getText().toString(), mSpanController.mOldSpanId
            )
            mSpanController2.mOldSpanId = id
            //如果当前span身上有值，先赋值给et身上
            binding.etSpan2.setText(
                    if (TextUtils.isEmpty(span.mText)) "" else span.mText
            )
            binding.etSpan2.setSelection(binding.etSpan.getText().length)
            span.mText = ""
            //通过rf计算出et当前应该显示的位置
            val rf: RectF = mSpanController2.getSpanRect(span)
            //设置EditText填空题中的相对位置
            mSpanController2.setEditTextRectF(rf)
        } else if (v.id == R.id.tv_span3) {
            if (id.equals("tv_one")) {
                showPicker(span, arrayListOf("文本a", "文本b"), id, mSpanController3)
            }
        } else if (v.id == R.id.tv_span4) {
            binding.etSpan4.visibility = View.VISIBLE
            mSpanController4.setSpanChecked(id)
            mSpanController4.setData(binding.etSpan4.text.toString(), mSpanController4.mOldSpanId)
            mSpanController4.mOldSpanId = id
            binding.etSpan4.setText(if (TextUtils.isEmpty(span.mText)) "" else span.mText)
            binding.etSpan4.setSelection(binding.etSpan4.text.length)
            span.mText = ""
            val rf: RectF = mSpanController4.getSpanRect(span)
            mSpanController4.setEditTextRectF(rf)
        }
    }

    private fun showPicker(span: RectSpan, list: List<String>, id: String, spanController: SpanController) {
        if (list == null) {
            return
        }
        val picker: OptionsPickerView<String> = OptionsPickerBuilder(
                this
        ) { options1, options2, options3, v ->
            if (span != null) {
                spanController.setData(list[options1], id)
                span.mText = list[options1]
                vo.z = list[options1]
            }
        }.setSelectOptions(0)
                .setCancelColor(Color.parseColor("#fdc915"))
                .setSubmitColor(Color.parseColor("#fdc915"))
                .setOutSideCancelable(false)
                .build<String>()
        picker.setPicker(list)
        picker.show()
    }

    fun hideInputMethod(){
        val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null) {
            imm.hideSoftInputFromWindow(
                    getWindow().getDecorView().getWindowToken(), 0
            )
        }
    }

    fun checkInfo(){
        hideInputMethod()
        mSpanController.setLastCheckedSpanText(binding.etSpan.text.toString())

        val contractResult = mSpanController.getResult()
        if (contractResult[0].isNullOrEmpty()) {
            Toast.makeText(this, "第1条不能为空", Toast.LENGTH_SHORT).show()
            return
        } else {
            vo.x = contractResult[0]
        }
        if (contractResult[1].isNullOrEmpty()) {
            Toast.makeText(this, "第2条不能为空", Toast.LENGTH_SHORT).show()
            return
        } else {
            vo.y = contractResult[1]
        }
        if (contractResult[2].isNullOrEmpty()) {
            Toast.makeText(this, "第3条不能为空", Toast.LENGTH_SHORT).show()
            return
        } else {
            vo.z = contractResult[2]
        }
        Log.i("vo", "${vo.toString()}")
    }
}