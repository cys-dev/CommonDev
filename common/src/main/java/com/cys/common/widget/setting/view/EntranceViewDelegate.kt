package com.cys.common.widget.setting.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingEntranceBinding
import com.cys.common.widget.other.BindingViewDelegate
import com.cys.common.widget.setting.SettingActivity
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory

class EntranceViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : BindingViewDelegate<SettingFactory.SettingItem, SettingEntranceBinding>() {

    override fun onCreateBinding(context: Context, parent: ViewGroup): SettingEntranceBinding {
        return SettingEntranceBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindBinding(binding: SettingEntranceBinding, item: SettingFactory.SettingItem) =
        with(binding) {
            settingTitle.text = item.title
            settingSummary.text = item.summary
            settingSummary.visibility = if (item.summary.isEmpty()) View.GONE else View.VISIBLE
            settingIcon.visibility = if (item.showEntranceIcon) View.VISIBLE else View.GONE
            root.setOnClickListener { v ->
                callback?.onEntranceClicked(item.id)
                item.entrance?.let {
                    SettingActivity.start(v.context, it)
                }
            }
        }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.id.toLong()
    }
}