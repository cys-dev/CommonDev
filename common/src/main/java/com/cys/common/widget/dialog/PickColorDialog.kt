package com.cys.common.widget.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.const.Colors
import com.cys.common.databinding.DialogPickColorBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.config.ConfigConst
import com.cys.common.utils.config.ConfigUtils
import com.cys.common.widget.other.ColorPlaceView
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

// TODO 使用Chip实现
class PickColorDialog(context: Context) : MessageDialog(context) {

    private val pickerBinding = DialogPickColorBinding.inflate(layoutInflater)
    private val colorPlaceList = ArrayList<ColorPlaceView>()
    private val defaultColorList = arrayListOf(
        Colors.WHITE,
        Colors.BLUE,
        Colors.BROWN,
        Colors.GRAY,
        Colors.GREEN,
        Colors.YELLOW,
        Colors.RED,
        Colors.ORANGE,
        Colors.PURPLE
    )
    private var resultColor = -1

    var inputColor = -1

    init {
        cancelButtonText = "自定义"
        cancelButtonVisible = View.VISIBLE
    }

    private val clickListener = View.OnClickListener {
        checkOne(it as ColorPlaceView)
    }

    private val changeListener =
        Slider.OnChangeListener { _, _, fromUser ->
            if (fromUser) {
                val fillColor = getSliderColor()
                setResultColor(fillColor, false)
            }
        }

    private val formatter = LabelFormatter { it.roundToInt().toString() }

    override fun initContentView() {
        with(pickerBinding) {
            colorPlaceList.clear()
            defaultColorList.forEach {
                val view = generateColorPlace(it)
                view.setOnClickListener(clickListener)
                colorPlaceList.add(view)
                dialogFlexbox.addView(view)
                if (inputColor == it) {
                    checkOne(view)
                }
            }

            val customColorList = ConfigUtils.getIntList(ConfigConst.KEY_CUSTOM_COLOR)
            customColorList.forEach {
                val view = generateColorPlace(it)
                view.setOnClickListener(clickListener)
                colorPlaceList.add(view)
                dialogFlexbox.addView(view)
                if (inputColor == it) {
                    checkOne(view)
                }
            }

            if (inputColor == -1 || resultColor == -1) {
                checkOne(colorPlaceList.firstOrNull())
            }

            dialogSliderRed.setLabelFormatter(formatter)
            dialogSliderGreen.setLabelFormatter(formatter)
            dialogSliderBlue.setLabelFormatter(formatter)
            dialogSliderAlpha.setLabelFormatter(formatter)

            dialogSliderRed.addOnChangeListener(changeListener)
            dialogSliderGreen.addOnChangeListener(changeListener)
            dialogSliderBlue.addOnChangeListener(changeListener)
            dialogSliderAlpha.addOnChangeListener(changeListener)
            addContentView(root)
        }
    }

    private fun generateColorPlace(@ColorInt fillColor: Int): ColorPlaceView {
        val view = ColorPlaceView(context)
        val size = 40.dp2Px()
        val margin = 5.dp2Px()
        val lp = ViewGroup.MarginLayoutParams(size, size)
        lp.setMargins(margin, margin, margin, margin)
        view.layoutParams = lp
        view.applyColor(fillColor, ContextCompat.getColor(context, R.color.blue_50), 3.dp2Px())
        return view
    }

    private fun checkOne(view: ColorPlaceView?, updateSlider: Boolean = true) {
        if (view == null) {
            return
        }
        colorPlaceList.forEach { colorPlaceView -> colorPlaceView.uncheck() }
        view.check()
        setResultColor(view.fillColor, updateSlider)
    }

    private fun setResultColor(@ColorInt fillColor: Int, updateSlider: Boolean = true) {
        resultColor = fillColor

        val alpha = (fillColor shr 24 and 0xFF) * 100 / 255
        val red = fillColor shr 16 and 0xFF
        val green = fillColor shr 8 and 0xFF
        val blue = fillColor shr 0 and 0xFF
        if (updateSlider) {
            with(pickerBinding) {
                dialogSliderRed.value = red.toFloat()
                dialogSliderGreen.value = green.toFloat()
                dialogSliderBlue.value = blue.toFloat()
                dialogSliderAlpha.value = alpha.toFloat()
            }
        }

        binding.dialogConfirm.setStyle(
            buttonRadius,
            if (alpha < 30 || (red + green + blue) > 600) confirmButtonTextColor else Color.WHITE,
            resultColor,
            confirmButtonPressedColor
        )
    }

    private fun saveResultColor() {
        val customColorList = ConfigUtils.getIntList(ConfigConst.KEY_CUSTOM_COLOR)
        if (!customColorList.contains(resultColor) && !defaultColorList.contains(resultColor)) {
            customColorList.add(resultColor)
        }
        if (customColorList.size > ConfigConst.KEY_CUSTOM_COLOR_MAX_SIZE) {
            customColorList.remove(customColorList.first())
        }
        ConfigUtils.setIntLinkedList(ConfigConst.KEY_CUSTOM_COLOR, customColorList)
    }

    @ColorInt
    private fun getSliderColor(): Int {
        with(pickerBinding) {
            val alpha = (dialogSliderAlpha.value * 2.55).roundToInt() shl 24
            val red = dialogSliderRed.value.roundToInt() shl 16
            val green = dialogSliderGreen.value.roundToInt() shl 8
            val blue = dialogSliderBlue.value.roundToInt() shl 0
            return alpha or red or green or blue
        }
    }

    private fun showMoreSetting() {
        binding.dialogCancel.text = "收起"
        binding.dialogCancel.setOnClickListener {
            hideMoreSetting()
        }
        pickerBinding.dialogMoreSettingContainer.visibility = View.VISIBLE
    }

    private fun hideMoreSetting() {
        binding.dialogCancel.text = "自定义"
        binding.dialogCancel.setOnClickListener {
            showMoreSetting()
        }
        pickerBinding.dialogMoreSettingContainer.visibility = View.GONE
    }

    override fun initListener() {
        with(binding) {
            val onClickListener = View.OnClickListener {
                dismiss()
                when (it) {
                    dialogConfirm -> {
                        listener?.selectColor(resultColor)
                        saveResultColor()
                    }
                }
            }
            dialogConfirm.setOnClickListener(onClickListener)
        }
        hideMoreSetting()
    }
}