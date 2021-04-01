package com.cys.common.widget.setting.view

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingSliderBinding
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate
import java.math.BigDecimal

class SliderViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : ViewDelegate<SettingFactory.SettingItem, View>() {

    private lateinit var binding: SettingSliderBinding

    override fun onCreateView(context: Context): View {
        binding = SettingSliderBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onBindView(view: View, item: SettingFactory.SettingItem) = with(binding) {
        settingTitle.text = item.title
        settingSlider.valueFrom = item.sliderFrom
        settingSlider.valueTo = item.sliderTo
        settingSlider.value = ConfigUtils.getFloat(item.key, item.sliderDefault)
        settingSlider.setLabelFormatter {
            it.toBigDecimal().setScale(item.sliderScale, BigDecimal.ROUND_HALF_UP).toString()
        }
        settingSlider.thumbTintList = ColorStateList.valueOf(themeColor)
        settingSlider.trackActiveTintList =
            ColorStateList.valueOf(themeColor and 0x88FFFFFF.toInt())
        settingSlider.trackInactiveTintList = ColorStateList.valueOf(themeColor and 0x20FFFFFF)
        settingSlider.clearOnChangeListeners()
        settingSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                ConfigUtils.set(item.key, value)
                callback?.onSettingChanged(item.key)
            }
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.key.hashCode().toLong()
    }
}