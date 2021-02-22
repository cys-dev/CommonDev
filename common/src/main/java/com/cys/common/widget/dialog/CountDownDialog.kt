package com.cys.common.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import com.cys.common.databinding.DialogMessageBinding

@SuppressLint("InflateParams")
class CountDownDialog(context: Context) : MessageDialog(context) {
    var countDown = 10
    private var countDownTimer: CountDownTimer? = null

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
                    binding.dialogConfirm.isEnabled = confirmButtonEnabled
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
}