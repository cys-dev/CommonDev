package com.cys.common.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.databinding.DialogMessageBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils

@SuppressLint("InflateParams")
class MessageDialog(context: Context) : AlertDialog(context) {

    var title = ""
    var message = ""
    var messageGravity = Gravity.CENTER

    var dialogGravity = Gravity.BOTTOM

    var buttonRadius = 4.dp2Px()

    var confirmButtonText = "确定"
    var confirmButtonVisible = View.VISIBLE
    var confirmButtonEnabled = true
    var confirmButtonTextColor = ContextCompat.getColor(context, R.color.blue_90)
    var confirmButtonNormalColor = ContextCompat.getColor(context, R.color.gray_8)
    var confirmButtonPressedColor = ContextCompat.getColor(context, R.color.gray_20)

    var cancelButtonText = "取消"
    var cancelButtonVisible = View.GONE
    var cancelButtonEnabled = true
    var cancelButtonTextColor = ContextCompat.getColor(context, R.color.blue_60)
    var cancelButtonNormalColor = ContextCompat.getColor(context, R.color.gray_8)
    var cancelButtonPressedColor = ContextCompat.getColor(context, R.color.gray_20)

    var checked = false
    var checkboxText = ""
    var checkboxVisible = View.GONE
    var checkboxFillColor = ContextCompat.getColor(context, R.color.blue_90)
    var bindCheckboxToConfirm = false

    var countDown = 10
    var countDownTimer: CountDownTimer? = null

    var closeable = true

    var listener: ActionListener? = null

    private val binding: DialogMessageBinding =
        DialogMessageBinding.inflate(LayoutInflater.from(context))

    override fun create() {
        initView()
        setView(binding.root)
        super.create()
    }

    private fun initView() {
        with(binding) {
            dialogTitle.text = title
            dialogMessage.text = message
            dialogMessage.gravity = messageGravity

            dialogConfirm.visibility = confirmButtonVisible
            dialogConfirm.isEnabled = if (bindCheckboxToConfirm) checked else confirmButtonEnabled
            dialogConfirm.text = confirmButtonText
            dialogConfirm.setStyle(
                buttonRadius,
                confirmButtonTextColor,
                confirmButtonNormalColor,
                confirmButtonPressedColor
            )

            dialogCancel.visibility = cancelButtonVisible
            dialogCancel.isEnabled = cancelButtonEnabled
            dialogCancel.text = cancelButtonText
            dialogCancel.setStyle(
                buttonRadius,
                cancelButtonTextColor,
                cancelButtonNormalColor,
                cancelButtonPressedColor
            )

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

            window?.setGravity(dialogGravity)
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    android.R.color.transparent
                )
            )
            dialogContainer.background = ShapeUtils.getRadiusShape(15.dp2Px(), Color.WHITE)

            val onClickListener = View.OnClickListener {
                dismiss()
                when (it) {
                    dialogConfirm -> listener?.confirm()
                    dialogCancel -> listener?.cancel()
                }
            }
            dialogConfirm.setOnClickListener(onClickListener)
            dialogCancel.setOnClickListener(onClickListener)
            if (bindCheckboxToConfirm) {
                dialogCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    dialogConfirm.isEnabled = isChecked
                }
            }

            setCancelable(closeable)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun show() {
        super.show()
        if (countDown > 0) {
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(countDown * 1000L, 1000L) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.dialogConfirm.text =
                        "${confirmButtonText}(${millisUntilFinished / 1000}s)"
                }

                override fun onFinish() {
                    binding.dialogConfirm.text = confirmButtonText
                    binding.dialogConfirm.isEnabled =
                        if (bindCheckboxToConfirm) binding.dialogCheckbox.isChecked else confirmButtonEnabled
                }
            }
            countDownTimer?.start()
            binding.dialogConfirm.isEnabled = false
            binding.dialogConfirm.text = "${confirmButtonText}（${countDown}s）"
        }
    }

    override fun dismiss() {
        countDownTimer?.cancel()
        super.dismiss()
    }

    inline fun show(func: MessageDialog.() -> Unit): MessageDialog {
        func()
        create()
        show()
        return this
    }

    interface ActionListener {
        fun confirm()
        fun cancel()
    }
}