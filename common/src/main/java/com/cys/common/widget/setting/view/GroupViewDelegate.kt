package com.cys.common.widget.setting.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingGroupTitleBinding
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate

class GroupViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : ViewDelegate<SettingFactory.SettingItem, View>() {

    private lateinit var binding: SettingGroupTitleBinding

    override fun onCreateView(context: Context): View {
        binding = SettingGroupTitleBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onBindView(view: View, item: SettingFactory.SettingItem) = with(binding) {
        settingTitle.text = item.title
        settingGroupDivider.visibility = if (item.groupDivider) View.VISIBLE else View.GONE
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.key.hashCode().toLong()
    }
}