package com.cys.common.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.databinding.DialogMessageBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils

@SuppressLint("InflateParams")
open class MessageDialog(context: Context) : AlertDialog(context) {
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

    var closeable = true

    var listener: DialogListener? = null

    protected val binding: DialogMessageBinding =
        DialogMessageBinding.inflate(LayoutInflater.from(context))

    override fun show() {
        create()
        super.show()
    }

    override fun create() {
        initWindow()
        initTitleAndMessage()
        initConfirmButton()
        initCancelButton()
        initListener()
        setCancelable(closeable)
        initContentView()
        setView(binding.root)
        super.create()
    }

    open fun initContentView() {}

    fun addContentView(view: View) {
        binding.dialogContent.removeAllViews()
        binding.dialogContent.addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    open fun initWindow() {
        with(binding) {
            window?.setGravity(dialogGravity)
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    android.R.color.transparent
                )
            )
            dialogContainer.background = ShapeUtils.getRadiusShape(15.dp2Px(), Color.WHITE)
        }
    }

    open fun initTitleAndMessage() {
        with(binding) {
            dialogTitle.text = title
            dialogMessage.text = message
            dialogMessage.gravity = messageGravity
            dialogMessage.visibility = if (message.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    open fun initConfirmButton() {
        with(binding) {
            dialogConfirm.visibility = confirmButtonVisible
            dialogConfirm.isEnabled = confirmButtonEnabled
            dialogConfirm.text = confirmButtonText
            dialogConfirm.setStyle(
                buttonRadius,
                confirmButtonTextColor,
                confirmButtonNormalColor,
                confirmButtonPressedColor
            )
        }
    }

    open fun initCancelButton() {
        with(binding) {
            dialogCancel.visibility = cancelButtonVisible
            dialogCancel.isEnabled = cancelButtonEnabled
            dialogCancel.text = cancelButtonText
            dialogCancel.setStyle(
                buttonRadius,
                cancelButtonTextColor,
                cancelButtonNormalColor,
                cancelButtonPressedColor
            )
        }
    }

    open fun initListener() {
        with(binding) {
            val onClickListener = View.OnClickListener {
                dismiss()
                when (it) {
                    dialogConfirm -> listener?.confirm()
                    dialogCancel -> listener?.cancel()
                }
            }
            dialogConfirm.setOnClickListener(onClickListener)
            dialogCancel.setOnClickListener(onClickListener)
        }
    }
}