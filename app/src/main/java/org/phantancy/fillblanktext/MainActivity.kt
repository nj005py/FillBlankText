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
import org.phantancy.fill_blank_text.span.RectSpan
import org.phantancy.fillblanktext.databinding.ActivityMainBinding
import org.phantancy.fillblanktext.entity.ContractVO

class MainActivity : AppCompatActivity(),RectSpan.OnClickListener {
    lateinit var binding: ActivityMainBinding
    lateinit var mSpanManager: SpanController
    var vo: ContractVO = ContractVO()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        var list:ArrayList<String>? = arrayListOf("list")
//        startActivity(
//            Intent(this, SecondActivity::class.java)
//                .putStringArrayListExtra("list", list)
//        )
        var defaultValues = arrayListOf<String>("","","txt1")
        var blankList = arrayListOf<BlankEntity>(
            BlankEntity("<EditText>","",100,30),
            BlankEntity("<EditText>","",100,30),
            BlankEntity("<TextView>","长长长长长长长长文本a",200,30)
        )
        val content =
            "<font color='red'><big>*</big></font>1、第一期租金于签合同当日交纳，下次付租日为每期应付日前${blankList[0].tag}天，乙方如逾期支付租金，每逾期一天，则乙方需按日租金的${blankList[1].tag}%支付滞纳金。协商不成，任一方均可${blankList[2].tag}"
        mSpanManager = SpanController(this, binding.tvSpan, binding.etSpan)
        mSpanManager.fillBlank(content,blankList)

        binding.btnSave.setOnClickListener { v ->
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null) {
                imm.hideSoftInputFromWindow(
                    getWindow().getDecorView().getWindowToken(), 0
                )
            }
            mSpanManager.setLastCheckedSpanText(binding.etSpan.text.toString())

            val contractResult = mSpanManager.getResult()
            if (contractResult[0].isNullOrEmpty()) {
                Toast.makeText(this, "第1条不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                vo.x = contractResult[0]
            }
            if (contractResult[1].isNullOrEmpty()) {
                Toast.makeText(this, "第2条不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                vo.y = contractResult[1]
            }
            if (contractResult[2].isNullOrEmpty()) {
                Toast.makeText(this, "第3条不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                vo.z = contractResult[2]
            }
            Log.i("vo", "${vo.toString()}")
        }
//        vo.apply {
//            x = "1"
//            y = "2"
//            z = "zzz"
//        }
//
//        vo.x?.let { mSpanManager.setData(it, null, 0) }
//        vo.y?.let { mSpanManager.setData(it, null, 1) }
//        vo.z?.let { mSpanManager.setData(it, null, 2) }

    }

    override fun OnClick(v: TextView, id: Int, span: RectSpan) {
        if (v.id == R.id.tv_span) {
            if (id == 2) {
                binding.etSpan.visibility = View.GONE
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) {
                    imm.hideSoftInputFromWindow(
                        getWindow().getDecorView().getWindowToken(), 0
                    )
                }
                mSpanManager.setData(
                    binding.etSpan.getText().toString(),
                    null,
                    mSpanManager.mOldSpan
                )
                showPicker(span, arrayListOf("长长长长长长长长文本a", "长长长长长长长长文本b"), id)
            } else{
                binding.etSpan.visibility = View.VISIBLE
                //设置选中的id
                mSpanManager.setSpanChecked(id)
                mSpanManager.setData(
                    binding.etSpan.getText().toString(),
                    null,
                    mSpanManager.mOldSpan
                )
                mSpanManager.mOldSpan = id
                //如果当前span身上有值，先赋值给et身上
                binding.etSpan.setText(
                    if (TextUtils.isEmpty(span.mText)) "" else span.mText
                )
                binding.etSpan.setSelection(binding.etSpan.getText().length)
                span.mText = ""
                //通过rf计算出et当前应该显示的位置
                val rf: RectF = mSpanManager.getSpanRect(span)
                //设置EditText填空题中的相对位置
                mSpanManager.setEtXY(rf)
            }
        }
    }

    private fun showPicker(span: RectSpan, list: List<String>, id: Int) {
        if (list == null) {
            return
        }
        val picker: OptionsPickerView<String> = OptionsPickerBuilder(
            this
        ) { options1, options2, options3, v ->
            if (span != null) {


                mSpanManager.setData(list[options1], null, id)
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
}