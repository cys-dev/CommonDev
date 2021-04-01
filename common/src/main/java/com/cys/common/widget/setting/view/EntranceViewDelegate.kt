package com.cys.common.widget.setting.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingEntranceBinding
import com.cys.common.widget.setting.SettingActivity
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate

class EntranceViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : ViewDelegate<SettingFactory.SettingItem, View>() {

    private lateinit var binding: SettingEntranceBinding

    override fun onCreateView(context: Context): View {
        binding = SettingEntranceBinding.inflate(LayoutInflater.from(context))
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

        root.setOnClickListener { v ->
            item.entrance?.let {
                SettingActivity.start(v.context, it)
            }
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.key.hashCode().toLong()
    }
}