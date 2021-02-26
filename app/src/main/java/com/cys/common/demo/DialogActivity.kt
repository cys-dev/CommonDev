package com.cys.common.demo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cys.common.databinding.ActivityDialogBinding
import com.cys.common.extends.showToast
import com.cys.common.widget.dialog.*

class DialogActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.buttonMessage.setOnClickListener(this)
        binding.buttonChecked.setOnClickListener(this)
        binding.buttonCountDown.setOnClickListener(this)
        binding.buttonChoose.setOnClickListener(this)
        binding.buttonInput.setOnClickListener(this)
        binding.buttonColorPick.setOnClickListener(this)
    }

    private val dialogListener = DialogListener().register {
        onConfirm = {
            showToast("点击确认")
        }
        onCancel = {
            showToast("点击取消")
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.buttonMessage -> {
                MessageDialog(this).apply {
                    title = "消息"
                    message = "这是一条振奋人心的消息！"
                    messageGravity = Gravity.CENTER
                    listener = dialogListener
                }.show()
            }
            binding.buttonChecked -> {
                MessageCheckDialog(this).apply {
                    title = "消息"
                    message = "这是一条振奋人心的消息！"
                    messageGravity = Gravity.START
                    listener = dialogListener
                    checkboxText = "朕知道了"
                    checkboxVisible = View.VISIBLE
                }.show()
            }
            binding.buttonCountDown -> {
                CountDownDialog(this).apply {
                    title = "消息"
                    message = "这是一条振奋人心的消息！"
                    messageGravity = Gravity.START
                    listener = dialogListener
                    countDown = 10
                }.show()
            }
            binding.buttonChoose -> {
                ChooseDialog(this).apply {
                    title = "选择"
                    message = ""
                    messageGravity = Gravity.START
                    chooseItems = arrayListOf("选项一", "选项二", "选项三")
                    chooseIndex = 1
                    listener = dialogListener
                }.show()
            }
            binding.buttonInput -> {
                InputDialog(this).apply {
                    title = "输入2"
                    message = "请输入您的电话号码"
                    messageGravity = Gravity.START
                    listener = dialogListener
                }.show()
            }
            binding.buttonColorPick -> {
                PickColorDialog(this).apply {
                    title = "选择颜色"
                    message = ""
                    messageGravity = Gravity.START
                    listener = dialogListener
                }.show()
            }
        }
    }

}