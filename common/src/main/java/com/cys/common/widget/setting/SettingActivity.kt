package com.cys.common.widget.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.cys.common.ColorfulActivity
import com.cys.common.const.Colors
import com.cys.common.databinding.ActivitySettingBinding

class SettingActivity : ColorfulActivity() {
    private lateinit var binding: ActivitySettingBinding

    companion object {
        private const val EXTRA_SETTING_FACTORY = "EXTRA_SETTING_FACTORY"
        fun start(context: Context, factory: SettingFactory) {
            try {
                val json = SettingFactory.toJson(factory)
                val intent = Intent(context, SettingActivity::class.java)
                    .putExtra(EXTRA_SETTING_FACTORY, json)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getColorConfig(): ColorConfig {
        return ColorConfig(Colors.WHITE, Colors.DARK_GRAY, "", true)
    }

    override fun getContentView(): View {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun initView() {
        if (intent != null) {
            val json = intent.getStringExtra(EXTRA_SETTING_FACTORY)
            json?.let {
                val factory = SettingFactory.fromJson(json)
                factory?.let {
                    binding.settingView.initView(it)
                    title = it.title
                }
                return
            }
        }
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}