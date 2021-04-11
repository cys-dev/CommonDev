package com.cys.common.widget.setting.view

import android.content.Context
import android.view.*
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingChooseBinding
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.dialog.ChooseDialog
import com.cys.common.widget.dialog.DialogListener
import com.cys.common.widget.other.BindingViewDelegate
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate

class ChooseViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : BindingViewDelegate<SettingFactory.SettingItem, SettingChooseBinding>() {

    override fun onCreateBinding(context: Context, parent: ViewGroup): SettingChooseBinding {
        return SettingChooseBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindBinding(binding: SettingChooseBinding, item: SettingFactory.SettingItem) = with(binding) {
        settingTitle.text = item.title
        if (item.key.isNotEmpty()) {
            settingSummary.text = ConfigUtils.getString(item.key, item.chooseDefault)
        } else {
            settingSummary.text = item.chooseDefault
        }
        settingSummary.visibility = if (settingSummary.text.isEmpty()) View.GONE else View.VISIBLE

        root.setOnClickListener {
            var idx = 0
            val chooseItem = if (item.key.isNotEmpty()) {
                ConfigUtils.getString(item.key, item.chooseDefault)
            } else {
                item.chooseDefault
            }
            if (item.chooseItems.contains(chooseItem)) {
                idx = item.chooseItems.indexOf(chooseItem)
            }
            ChooseDialog(binding.root.context).apply {
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
                        if (item.key.isNotEmpty()) {
                            ConfigUtils.set(item.key, item.summary)
                        }
                        callback?.onChooseChanged(item.id, item.summary)
                    }
                }
            }.show()
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.id.toLong()
    }
}