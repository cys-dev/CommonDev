package com.cys.common.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cys.common.const.Colors
import com.cys.common.databinding.ActivityMainBinding
import com.cys.common.widget.setting.SettingActivity
import com.cys.common.widget.setting.SettingFactory

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
        binding.dialogSetting.setOnClickListener {
            val factoryEntrance = SettingFactory().apply {
                title = "Demo设置内页"
                themeColor = Colors.BLUE
                noticeIndex = 2
                addChoose(
                    0,
                    "电量百分比",
                    arrayListOf("在电池图标外", "在电池图标内", "不显示"),
                    "在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内"
                )
                addSwitch(1, "显示实时网速", true, "在状态栏显示实时网速")
                addSlider(2, "音量", 0f, 100f, 50f)
                addChoose(3, "电量百分比", arrayListOf("在电池图标外", "在电池图标内", "不显示"), "在电池图标内")
            }
            val factory = SettingFactory().apply {
                title = "Demo设置主页"
                themeColor = Colors.BLUE
                noticeIndex = 2
                addChoose(
                    0,
                    "电量百分比",
                    arrayListOf("在电池图标外", "在电池图标内", "不显示"),
                    "在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内在电池图标内"
                )
                addGroupTitle(1, "账号")
                addSwitch(2, "显示实时网速", true, "在状态栏显示实时网速")
                addSlider(3, "音量", 0f, 100f, 50f)
                addChoose(4, "电量百分比", arrayListOf("在电池图标外", "在电池图标内", "不显示"), "在电池图标内")
                addEntrance(5, "更多设置", factoryEntrance)
                addColorPicker(6, "选择颜色", Colors.RED)
                addColorPicker(7, "选择颜色2", Colors.BLUE)
                addTextInput(8, "输入文本", "123")
            }
            SettingActivity.start(this, factory)
        }
    }
}