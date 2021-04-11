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
import com.cys.common.widget.other.BindingViewDelegate
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory

class SwitchViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : BindingViewDelegate<SettingFactory.SettingItem, SettingSwitchBinding>() {

    override fun onCreateBinding(context: Context, parent: ViewGroup): SettingSwitchBinding {
        return SettingSwitchBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindBinding(binding: SettingSwitchBinding, item: SettingFactory.SettingItem) =
        with(binding) {
            settingTitle.text = item.title
            settingSummary.text = item.summary
            settingSummary.visibility = if (item.summary.isEmpty()) View.GONE else View.VISIBLE

            settingSwitch.thumbTintList =
                ShapeUtils.getColorStateList(
                    android.R.attr.state_checked,
                    themeColor,
                    0,
                    ContextCompat.getColor(root.context, R.color.gray_medium)
                )
            settingSwitch.trackTintList =
                ShapeUtils.getColorStateList(
                    android.R.attr.state_checked,
                    ContextCompat.getColor(root.context, R.color.gray_medium),
                    0,
                    ContextCompat.getColor(root.context, R.color.gray_medium)
                )
            if (item.key.isNotEmpty()) {
                settingSwitch.isChecked = ConfigUtils.getBoolean(item.key, item.switchChecked)
            } else {
                settingSwitch.isChecked = item.switchChecked
            }
            settingSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (item.key.isNotEmpty()) {
                    ConfigUtils.set(item.key, isChecked)
                }
                callback?.onSwitchChanged(item.id, isChecked)
            }
            root.setOnClickListener {
                callback?.let {
                    binding.settingSwitch.isChecked = !binding.settingSwitch.isChecked
                }
            }
        }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.id.toLong()
    }
}