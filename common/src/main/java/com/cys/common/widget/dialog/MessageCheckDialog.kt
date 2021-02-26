package com.cys.common.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.databinding.DialogCheckBinding
import com.cys.common.utils.ShapeUtils

@SuppressLint("InflateParams")
class MessageCheckDialog(context: Context) : MessageDialog(context) {

    var checked = false
    var checkboxText = ""
    var checkboxVisible = View.VISIBLE
    var checkboxFillColor = ContextCompat.getColor(context, R.color.blue_90)
    var bindCheckboxToConfirm = false

    private val checkBinding = DialogCheckBinding.inflate(layoutInflater)

    override fun initContentView() {
        initCheckbox()
    }

    override fun initConfirmButton() {
        super.initConfirmButton()
        binding.dialogConfirm.isEnabled =
            if (bindCheckboxToConfirm) checked else confirmButtonEnabled
    }

    private fun initCheckbox() {
        with(checkBinding) {
            dialogCheckbox.isChecked = checked
            dialogCheckbox.text = checkboxText
            dialogCheckbox.visibility = checkboxVisible

            val uncheck = ContextCompat.getDrawable(context, R.drawable.ic_uncheck)
            val check = ContextCompat.getDrawable(context, R.drawable.ic_check)

            if (uncheck != null && check != null) {
                val colorStateList = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(checkboxFillColor, ContextCompat.getColor(context, R.color.gray_60))
                )
                dialogCheckbox.buttonDrawable = ShapeUtils.getColorStateListDrawable(
                    uncheck,
                    check,
                    android.R.attr.state_checked
                )
                dialogCheckbox.buttonTintList = colorStateList
            }
            addContentView(checkBinding.root)
        }
    }

    override fun initListener() {
        super.initListener()
        with(checkBinding) {
            if (bindCheckboxToConfirm) {
                dialogCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    binding.dialogConfirm.isEnabled = isChecked
                }
            }
        }
    }
}