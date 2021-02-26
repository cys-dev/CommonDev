package com.cys.common.demo

import android.content.Intent
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
            startActivity(Intent(this, DialogActivity::class.java))
        }
    }
}