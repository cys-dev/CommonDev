package com.cys.common.widget.button

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils

class TextButton : AppCompatTextView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.TextButton, 0, 0)
        val radius = arr.getDimensionPixelOffset(R.styleable.TextButton_radius, 4)
        val normalColor = arr.getColor(
            R.styleable.TextButton_normalColor,
            ContextCompat.getColor(context, R.color.gray_20)
        )
        val pressedColor = arr.getColor(
            R.styleable.TextButton_pressedColor,
            ContextCompat.getColor(context, R.color.gray_40)
        )
        arr.recycle()
        setStyle(radius, textColors.defaultColor, normalColor, pressedColor)
    }

    fun setStyle(
        radius: Int = 4.dp2Px(),
        @ColorInt textColor: Int,
        @ColorInt normalColor: Int,
        @ColorInt pressedColor: Int
    ) {
        val normalDrawable = ShapeUtils.getRadiusShape(radius, normalColor)
        val pressedDrawable = ShapeUtils.getRadiusShape(radius, pressedColor)
        this.background = ShapeUtils.getColorStateListDrawable(normalDrawable, pressedDrawable)

        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf()),
            intArrayOf(textColor, ContextCompat.getColor(context, R.color.gray_90))
        )
        this.setTextColor(colorStateList)
    }
}