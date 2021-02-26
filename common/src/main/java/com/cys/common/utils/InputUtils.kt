package com.cys.common.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object InputUtils {

    fun showSoftInput(view: View) {
        view.isFocusable = true;
        view.isFocusableInTouchMode = true;
        view.requestFocus();
        val imm: InputMethodManager =
            view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    fun hideSoftInput(view: View) {
        val imm: InputMethodManager =
            view.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}