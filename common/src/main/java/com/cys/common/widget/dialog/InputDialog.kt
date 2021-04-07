package com.cys.common.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import com.cys.common.R
import com.cys.common.databinding.DialogInputBinding
import com.cys.common.utils.InputUtils

@SuppressLint("InflateParams")
class InputDialog(context: Context) : MessageDialog(context) {

    private val inputBinding = DialogInputBinding.inflate(layoutInflater)
    val editText = inputBinding.dialogEditText
    var themeColor = ContextCompat.getColor(context, R.color.blue_80)
    var inputText = ""

    override fun initContentView() {
        initEditText()
    }

    private fun initEditText() {
        with(inputBinding) {
            dialogEditText.setText(inputText)
            dialogEditText.setBaseColor(themeColor)
            dialogEditText.setPrimaryColor(themeColor)
            val drawable = ContextCompat.getDrawable(context, R.drawable.edit_text_cursor)
            drawable?.setTint(themeColor)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                dialogEditText.textCursorDrawable = drawable
            }
            addContentView(root)
        }
    }

    override fun initListener() {
        super.initListener()
        this.setOnShowListener {
            binding.root.postDelayed({
                InputUtils.showSoftInput(inputBinding.dialogEditText)
            }, 100)
        }
        val onClickListener = View.OnClickListener {
            dismiss()
            when (it) {
                binding.dialogConfirm -> listener?.inputText(editText.text.toString())
            }
        }
        binding.dialogConfirm.setOnClickListener(onClickListener)
    }

    override fun dismiss() {
        InputUtils.hideSoftInput(inputBinding.dialogEditText)
        super.dismiss()
    }
}