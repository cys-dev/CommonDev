package com.cys.common.widget.setting.view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.cys.common.databinding.SettingColorBinding
import com.cys.common.databinding.SettingInputBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.dialog.DialogListener
import com.cys.common.widget.dialog.InputDialog
import com.cys.common.widget.setting.SettingCallback
import com.cys.common.widget.setting.SettingFactory
import com.drakeet.multitype.ViewDelegate

class InputViewDelegate(
    private val callback: SettingCallback? = null,
    @ColorInt private val themeColor: Int
) : ViewDelegate<SettingFactory.SettingItem, View>() {

    private lateinit var binding: SettingInputBinding
    private var itemKey = ""

    private val dialogListener = DialogListener().register {
        onInputText = {
            binding.settingSummary.text = it
            ConfigUtils.set(itemKey, it)
            callback?.onInputText(itemKey, it)
        }
    }

    override fun onCreateView(context: Context): View {
        binding = SettingInputBinding.inflate(LayoutInflater.from(context))
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onBindView(view: View, item: SettingFactory.SettingItem) = with(binding) {
        settingTitle.text = item.title
        item.summary = ConfigUtils.getString(item.key, item.summary)
        settingSummary.text = item.summary
        settingSummary.visibility = if (item.summary.isEmpty()) View.GONE else View.VISIBLE
        itemKey = item.key
        root.setOnClickListener { v ->
            val dialog = InputDialog(v.context).apply {
                title = "输入内容"
                message = ""
                inputText = settingSummary.text.toString()
                messageGravity = Gravity.START
                listener = dialogListener
            }
            dialog.editText.setText(item.summary)
            dialog.show()
        }
    }

    override fun getItemId(item: SettingFactory.SettingItem): Long {
        return item.key.hashCode().toLong()
    }
}