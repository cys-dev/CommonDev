package com.cys.common.widget.setting.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingGroupTitleBinding
import com.cys.common.widget.other.BindingViewDelegate
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory

class GroupViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : BindingViewDelegate<SettingFactory.SettingItem, SettingGroupTitleBinding>() {

    override fun onCreateBinding(context: Context, parent: ViewGroup): SettingGroupTitleBinding {
        return SettingGroupTitleBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindBinding(
        binding: SettingGroupTitleBinding,
        item: SettingFactory.SettingItem
    ) =
        with(binding) {
            settingTitle.text = item.title
            settingGroupDivider.visibility = if (item.groupDivider) View.VISIBLE else View.GONE
        }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.id.toLong()
    }
}