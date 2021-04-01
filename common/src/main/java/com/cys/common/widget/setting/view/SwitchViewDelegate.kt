package com.cys.common.widget.setting.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.databinding.SettingSwitchBinding
import com.cys.common.utils.ShapeUtils
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate

class SwitchViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : ViewDelegate<SettingFactory.SettingItem, View>() {

    private lateinit var binding: SettingSwitchBinding

    override fun onCreateView(context: Context): View {
        binding = SettingSwitchBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onBindView(view: View, item: SettingFactory.SettingItem) = with(binding) {
        settingTitle.text = item.title
        settingSummary.text = item.summary
        settingSummary.visibility = if (item.summary.isEmpty()) View.GONE else View.VISIBLE

        settingSwitch.thumbTintList =
            ShapeUtils.getColorStateList(
                android.R.attr.state_checked,
                themeColor,
                0,
                ContextCompat.getColor(view.context, R.color.gray_medium)
            )
        settingSwitch.trackTintList =
            ShapeUtils.getColorStateList(
                android.R.attr.state_checked,
                ContextCompat.getColor(view.context, R.color.gray_medium),
                0,
                ContextCompat.getColor(view.context, R.color.gray_medium)
            )
        settingSwitch.isChecked = ConfigUtils.getBoolean(item.key, item.switchChecked)
        settingSwitch.setOnCheckedChangeListener { _, isChecked ->
            ConfigUtils.set(item.key, isChecked)
            callback?.onSettingChanged(item.key)
        }
        root.setOnClickListener {
            binding.settingSwitch.isChecked = !binding.settingSwitch.isChecked
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.key.hashCode().toLong()
    }
}