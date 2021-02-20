package com.cys.common.demo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.databinding.ActivityMainBinding
import com.cys.common.widget.dialog.MessageDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.dialogButton.setOnClickListener {
            MessageDialog(this).show {
                title = "警告"
                message = "当前APP版本过低，无法继续运行，请点击“确定”进行升级！"
                messageGravity = Gravity.START
                checked = true
                checkboxText = "不再提醒"
                checkboxVisible = View.VISIBLE
                bindCheckboxToConfirm = true
                listener = object : MessageDialog.ActionListener {
                    override fun confirm() {

                    }

                    override fun cancel() {

                    }
                }
            }
        }
    }
}