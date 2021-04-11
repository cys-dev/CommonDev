package com.cys.common.widget.setting.view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.databinding.SettingColorBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.dialog.DialogListener
import com.cys.common.widget.dialog.PickColorDialog
import com.cys.common.widget.other.BindingViewDelegate
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory

class ColorViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : BindingViewDelegate<SettingFactory.SettingItem, SettingColorBinding>() {

    override fun onCreateBinding(context: Context, parent: ViewGroup): SettingColorBinding {
        return SettingColorBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindBinding(binding: SettingColorBinding, item: SettingFactory.SettingItem) {
        with(binding) {
            settingTitle.text = item.title
            settingSummary.text = item.summary
            settingSummary.visibility = if (item.summary.isEmpty()) View.GONE else View.VISIBLE
            val color = if (item.key.isNotEmpty()) {
                ConfigUtils.getInt(item.key, item.color)
            } else {
                item.color
            }
            settingColor.background = ShapeUtils.getRadiusStrokeShape(
                100.dp2Px(),
                color,
                1.dp2Px(),
                ContextCompat.getColor(root.context, R.color.gray_60)
            )
            root.setOnClickListener { v ->
                PickColorDialog(v.context).apply {
                    title = "选择颜色"
                    message = ""
                    inputColor = color
                    messageGravity = Gravity.START
                    listener = DialogListener().register {
                        onSelectColor = {
                            callback?.onColorChanged(item.id, it)
                            if (item.key.isNotEmpty()) {
                                ConfigUtils.set(item.key, it)
                            }
                            settingColor.background =
                                ShapeUtils.getRadiusStrokeShape(
                                    100.dp2Px(),
                                    it,
                                    1.dp2Px(),
                                    ContextCompat.getColor(context, R.color.gray_60)
                                )
                        }
                    }
                }.show()
            }
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.id.toLong()
    }
}