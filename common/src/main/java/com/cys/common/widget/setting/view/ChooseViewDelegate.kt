package com.cys.common.widget.setting.view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingChooseBinding
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.dialog.ChooseDialog
import com.cys.common.widget.dialog.DialogListener
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate

class ChooseViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : ViewDelegate<SettingFactory.SettingItem, View>() {

    private lateinit var binding: SettingChooseBinding

    override fun onCreateView(context: Context): View {
        binding = SettingChooseBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onBindView(view: View, item: SettingFactory.SettingItem) = with(binding) {
        settingTitle.text = item.title
        settingSummary.text = ConfigUtils.getString(item.key, item.chooseDefault)
        settingSummary.visibility = if (settingSummary.text.isEmpty()) View.GONE else View.VISIBLE

        root.setOnClickListener {
            var idx = 0
            val chooseItem = ConfigUtils.getString(item.key, item.chooseDefault)
            if (item.chooseItems.contains(chooseItem)) {
                idx = item.chooseItems.indexOf(chooseItem)
            }
            ChooseDialog(view.context).apply {
                title = item.title
                message = ""
                messageGravity = Gravity.START
                confirmButtonVisible = View.GONE
                cancelButtonVisible = View.VISIBLE
                chooseItems = item.chooseItems
                chooseIndex = idx
                listener = DialogListener().register {
                    onConfirm = {
                        item.summary = item.chooseItems[chooseIndex]
                        settingSummary.text = item.summary
                        ConfigUtils.set(item.key, item.summary)
                        callback?.onSettingChanged(item.key)
                    }
                }
            }.show()
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.key.hashCode().toLong()
    }
}