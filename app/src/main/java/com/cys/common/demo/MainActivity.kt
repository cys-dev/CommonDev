package com.cys.common.demo

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.databinding.ActivityMainBinding
import com.cys.common.widget.dialog.ChooseDialog
import com.cys.common.widget.dialog.DialogListener
import com.cys.common.widget.dialog.MessageCheckDialog

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
            ChooseDialog(this).apply {
                title = "警告"
                message = "这是一条信息"
                messageGravity = Gravity.START
                chooseItems = arrayListOf("123", "456", "789")
                chooseIndex = 1
                listener = DialogListener().register {
                    onConfirm = {

                    }
                }
            }.show()
        }
    }
}