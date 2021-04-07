package com.cys.common.widget.setting.view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.const.Colors
import com.cys.common.databinding.SettingColorBinding
import com.cys.common.databinding.SettingEntranceBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.dialog.DialogListener
import com.cys.common.widget.dialog.PickColorDialog
import com.cys.common.widget.setting.SettingActivity
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate

class ColorViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : ViewDelegate<SettingFactory.SettingItem, View>() {

    private lateinit var binding: SettingColorBinding
    private var itemKey = ""

    private val dialogListener = DialogListener().register {
        onSelectColor = {
            callback?.onColorChanged(itemKey, it)
            ConfigUtils.set(itemKey, it)
            binding.settingColor.background = ShapeUtils.getRadiusShape(100.dp2Px(), it)
        }
    }

    override fun onCreateView(context: Context): View {
        binding = SettingColorBinding.inflate(LayoutInflater.from(context))
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
        val color = ConfigUtils.getInt(item.key, item.color)
        settingColor.background = ShapeUtils.getRadiusShape(100.dp2Px(), color)
        itemKey = item.key
        root.setOnClickListener { v ->
            PickColorDialog(v.context).apply {
                title = "选择颜色"
                message = ""
                inputColor = color
                messageGravity = Gravity.START
                listener = dialogListener
            }.show()
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.key.hashCode().toLong()
    }
}