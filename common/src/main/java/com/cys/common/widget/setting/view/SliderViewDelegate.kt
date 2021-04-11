package com.cys.common.widget.setting.view

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingSliderBinding
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.other.BindingViewDelegate
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate
import java.math.BigDecimal

class SliderViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : BindingViewDelegate<SettingFactory.SettingItem, SettingSliderBinding>() {

    override fun onCreateBinding(context: Context, parent: ViewGroup): SettingSliderBinding {
        return SettingSliderBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindBinding(binding: SettingSliderBinding, item: SettingFactory.SettingItem) = with(binding) {
        settingTitle.text = item.title
        settingSlider.valueFrom = item.sliderFrom
        settingSlider.valueTo = item.sliderTo
        if (item.key.isNotEmpty()) {
            settingSlider.value = ConfigUtils.getFloat(item.key, item.sliderDefault)
        } else {
            settingSlider.value = item.sliderDefault
        }
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
                if (item.key.isNotEmpty()) {
                    ConfigUtils.set(item.key, value)
                }
                callback?.onSliderChanged(item.id, value)
            }
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.id.toLong()
    }
}