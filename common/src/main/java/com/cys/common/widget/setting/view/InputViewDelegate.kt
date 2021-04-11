package com.cys.common.widget.setting.view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingInputBinding
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.dialog.DialogListener
import com.cys.common.widget.dialog.InputDialog
import com.cys.common.widget.other.BindingViewDelegate
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory

class InputViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : BindingViewDelegate<SettingFactory.SettingItem, SettingInputBinding>() {

    override fun onCreateBinding(context: Context, parent: ViewGroup): SettingInputBinding {
        return SettingInputBinding.inflate(LayoutInflater.from(context), parent, false)
    }

    override fun onBindBinding(binding: SettingInputBinding, item: SettingFactory.SettingItem) =
        with(binding) {
            settingTitle.text = item.title
            if (item.key.isNotEmpty()) {
                item.summary = ConfigUtils.getString(item.key, item.summary)
            } else {
                item.summary = item.summary
            }
            settingSummary.text = item.summary
            settingSummary.visibility = if (item.summary.isEmpty()) View.GONE else View.VISIBLE
            root.setOnClickListener { v ->
                val dialog = InputDialog(v.context).apply {
                    title = "输入内容"
                    message = ""
                    inputText = settingSummary.text.toString()
                    messageGravity = Gravity.START
                    listener = DialogListener().register {
                        onInputText = {
                            binding.settingSummary.text = it
                            if (item.key.isNotEmpty()) {
                                item.summary = ConfigUtils.getString(item.key, item.summary)
                            }
                            callback?.onInputText(item.id, it)
                        }
                    }
                }
                dialog.editText.setText(item.summary)
                dialog.show()
            }
        }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.id.toLong()
    }
}