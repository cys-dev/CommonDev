package com.cys.common.widget.other

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.const.Colors
import com.cys.common.databinding.WidgetColorPlaceBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils

class ColorPlaceView : ConstraintLayout {

    private val binding: WidgetColorPlaceBinding
    var fillColor: Int
    private var selectorColor: Int
    private var selectorWidth: Int
    private var isChecked = false
    private val radius = 1000.dp2Px()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        binding = WidgetColorPlaceBinding.inflate(LayoutInflater.from(context))
        val typedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.ColorPlaceView, defStyleAttr, 0)
        fillColor = typedArray.getColor(R.styleable.ColorPlaceView_fillColor, Colors.GRAY)
        selectorColor = typedArray.getColor(R.styleable.ColorPlaceView_selectorColor, Colors.BLUE)
        selectorWidth =
            typedArray.getDimension(R.styleable.ColorPlaceView_selectorWidth, 6.dp2Px().toFloat())
                .toInt()
        typedArray.recycle()
        removeAllViews()
        addView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding) {
            colorFillContainer.background =
                ShapeUtils.getRadiusStrokeShape(
                    radius,
                    fillColor,
                    1.dp2Px(),
                    ContextCompat.getColor(context, R.color.gray_60)
                )
            colorSelectorContainer.background = if (isChecked) ShapeUtils.getRadiusStrokeShape(
                radius,
                Color.WHITE, selectorWidth, selectorColor
            ) else ContextCompat.getDrawable(context, android.R.color.transparent)
        }
    }

    fun applyColor(@ColorInt fillColor: Int, @ColorInt selectorColor: Int, selectorWidth: Int) {
        this.fillColor = fillColor
        this.selectorColor = selectorColor
        this.selectorWidth = selectorWidth
        initView()
    }

    fun check() {
        isChecked = true
        initView()
    }

    fun uncheck() {
        isChecked = false
        initView()
    }
}