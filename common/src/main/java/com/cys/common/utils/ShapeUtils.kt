package com.cys.common.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt

object ShapeUtils {

    fun getRadiusShape(radius: Int, @ColorInt color: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.cornerRadius = radius.toFloat()
        drawable.setColor(color)
        return drawable
    }

    fun getRadiusStrokeShape(
        radius: Int, @ColorInt bgColor: Int, strokeWidth: Int, @ColorInt strokeColor: Int
    ): GradientDrawable {
        val drawable = getRadiusShape(radius, bgColor)
        drawable.setStroke(strokeWidth, strokeColor)
        return drawable
    }

    fun setStateBackground(view: View?, @ColorInt color: Int) {
        if (view != null) {
            var background = view.background
            if (background == null) {
                background = ColorDrawable(Color.TRANSPARENT)
            }
            view.background = getColorStateListDrawable(
                background, ColorDrawable(color)
            )
        }
    }

    fun setStateBackground(view: View?, background: Drawable) {
        if (view != null) {
            var bg = view.background
            if (bg == null) {
                bg = ColorDrawable(Color.TRANSPARENT)
            }
            view.background = getColorStateListDrawable(
                bg, background
            )
        }
    }

    fun setStateTextColor(textView: TextView?, normalColor: Int, pressedColor: Int) {
        textView?.let {
            val states = Array(2) { IntArray(1) }
            states[0] = IntArray(1) { android.R.attr.state_pressed }
            states[1] = IntArray(1)
            val colors = IntArray(2)
            colors[0] = pressedColor
            colors[1] = normalColor
            textView.setTextColor(ColorStateList(states, colors))
        }
    }

    fun getColorStateListDrawable(
        background: Drawable, pressedBackground: Drawable, attr: Int = android.R.attr.state_pressed
    ): StateListDrawable {
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(
            IntArray(1) { attr }, pressedBackground
        )
        stateListDrawable.addState(IntArray(1), background)
        return stateListDrawable
    }

    fun getColorStateList(vararg values: Int): ColorStateList {
        val size = values.size / 2
        val states = Array(size) { IntArray(1) }
        val colors = IntArray(size)

        for (idx in 0 until size) {
            states[idx] = IntArray(1) { values[idx * 2] }
            colors[idx] = values[idx * 2 + 1]
        }
        return ColorStateList(states, colors)
    }
}