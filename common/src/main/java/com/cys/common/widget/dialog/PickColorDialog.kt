package com.cys.common.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import com.cys.common.databinding.DialogPickColorBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils
import com.cys.common.widget.other.ColorPickerView

@SuppressLint("InflateParams")
class PickColorDialog(context: Context) : MessageDialog(context) {

    private val pickerBinding = DialogPickColorBinding.inflate(layoutInflater)

    companion object {
        val RED = Color.parseColor("#d0021b")
        val RED_HIGH = Color.parseColor("#7a0210")
        val RED_LOW = Color.parseColor("#fd5469")

        val ORANGE = Color.parseColor("#f5a623")
        val ORANGE_HIGH = Color.parseColor("#9e6607")
        val ORANGE_LOW = Color.parseColor("#f9ca7b")

        val PURPLE = Color.parseColor("#bd10e0")
        val PURPLE_HIGH = Color.parseColor("#700a85")
        val PURPLE_LOW = Color.parseColor("#e58cf7")

        val YELLOW = Color.parseColor("#f8e71c")
        val YELLOW_HIGH = Color.parseColor("#9e9305")
        val YELLOW_LOW = Color.parseColor("#fcf49a")

        val GREEN = Color.parseColor("#7ed321")
        val GREEN_HIGH = Color.parseColor("#b5ea7b")
        val GREEN_LOW = Color.parseColor("#4b7d14")

        val BLUE = Color.parseColor("#4a90e2")
        val BLUE_HIGH = Color.parseColor("#9fc4f0")
        val BLUE_LOW = Color.parseColor("#185499")

        val BROWN = Color.parseColor("#8b572a")
        val BROWN_HIGH = Color.parseColor("#523319")
        val BROWN_LOW = Color.parseColor("#cc8f57")

        val GRAY = Color.parseColor("#9b9b9b")
        val GRAY_HIGH = Color.parseColor("#000000")
        val GRAY_LOW = Color.parseColor("#fbfbfb")
    }

    override fun initContentView() {
        initColorPicker()
    }

    private fun initColorPicker() {
        with(pickerBinding) {
            dialogColorPicker.showDefaultColorTable()
            setResultColor(dialogColorPicker.color)

            val radius = 5.dp2Px()
            dialogColorRed.background = ShapeUtils.getRadiusShape(radius, RED)
            dialogColorOrange.background = ShapeUtils.getRadiusShape(radius, ORANGE)
            dialogColorPurple.background = ShapeUtils.getRadiusShape(radius, PURPLE)
            dialogColorYellow.background = ShapeUtils.getRadiusShape(radius, YELLOW)
            dialogColorGreen.background = ShapeUtils.getRadiusShape(radius, GREEN)
            dialogColorBlue.background = ShapeUtils.getRadiusShape(radius, BLUE)
            dialogColorBrown.background = ShapeUtils.getRadiusShape(radius, BROWN)
            dialogColorGray.background = ShapeUtils.getRadiusShape(radius, GRAY)

            val onClickListener = View.OnClickListener {
                when (it) {
                    dialogColorRed -> {
                        dialogColorPicker.setColors(RED_LOW, RED, RED_HIGH)
                        setResultColor(RED)
                    }
                    dialogColorOrange -> {
                        dialogColorPicker.setColors(ORANGE_LOW, ORANGE, ORANGE_HIGH)
                        setResultColor(ORANGE)
                    }
                    dialogColorPurple -> {
                        dialogColorPicker.setColors(RED_LOW, RED, RED_HIGH)
                        setResultColor(RED)
                    }
                    dialogColorYellow -> {
                        dialogColorPicker.setColors(YELLOW_LOW, YELLOW, YELLOW_HIGH)
                        setResultColor(YELLOW)
                    }
                    dialogColorGreen -> {
                        dialogColorPicker.setColors(GREEN_LOW, GREEN, GREEN_HIGH)
                        setResultColor(GREEN)
                    }
                    dialogColorBlue -> {
                        dialogColorPicker.setColors(BLUE_LOW, BLUE, BLUE_HIGH)
                        setResultColor(BLUE)
                    }
                    dialogColorBrown -> {
                        dialogColorPicker.setColors(BROWN_LOW, BROWN, BROWN_HIGH)
                        setResultColor(BROWN)
                    }
                    dialogColorGray -> {
                        dialogColorPicker.setColors(GRAY_LOW, GRAY, GRAY_HIGH)
                        setResultColor(GRAY)
                    }
                }
                dialogColorPicker.setPosition(
                    dialogColorPicker.width / 2,
                    dialogColorPicker.height / 2
                )
            }
            dialogColorRed.setOnClickListener(onClickListener)
            dialogColorOrange.setOnClickListener(onClickListener)
            dialogColorPurple.setOnClickListener(onClickListener)
            dialogColorYellow.setOnClickListener(onClickListener)
            dialogColorGreen.setOnClickListener(onClickListener)
            dialogColorBlue.setOnClickListener(onClickListener)
            dialogColorBrown.setOnClickListener(onClickListener)
            dialogColorGray.setOnClickListener(onClickListener)

            dialogColorPicker.setOnColorPickerChangeListener(object :
                ColorPickerView.OnColorPickerChangeListener {
                override fun onColorChanged(picker: ColorPickerView?, color: Int) {
                    setResultColor(color)
                }

                override fun onStartTrackingTouch(picker: ColorPickerView?) {

                }

                override fun onStopTrackingTouch(picker: ColorPickerView?) {

                }
            })

            dialogAlphaPicker.addOnChangeListener { _, _, _ ->
                setResultColor(dialogColorPicker.color)
            }
            addContentView(root)
        }
    }

    private fun setResultColor(color: Int) {
        with(pickerBinding) {
            val alpha = String.format("%02X", (2.55 * dialogAlphaPicker.value).toInt())
            val colorStr = String.format("%08X", color).removeRange(0, 2)
            pickerBinding.dialogColorResult.background =
                ShapeUtils.getRadiusShape(100.dp2Px(), Color.parseColor("#$alpha$colorStr"))
        }
    }
}