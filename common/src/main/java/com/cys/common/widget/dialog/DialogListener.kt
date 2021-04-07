package com.cys.common.widget.dialog

import androidx.annotation.ColorInt

private typealias OnConfirm = () -> Unit
private typealias OnCancel = () -> Unit
private typealias OnSelectColor = (color: Int) -> Unit
private typealias OnInputText = (text: String) -> Unit

class DialogListener {

    var onConfirm: OnConfirm? = null
    var onCancel: OnCancel? = null
    var onSelectColor: OnSelectColor? = null
    var onInputText: OnInputText? = null

    fun confirm() {
        onConfirm?.invoke()
    }

    fun cancel() {
        onCancel?.invoke()
    }

    fun selectColor(@ColorInt color: Int) {
        onSelectColor?.invoke(color)
    }

    fun inputText(text: String) {
        onInputText?.invoke(text)
    }

    inline fun register(func: DialogListener.() -> Unit): DialogListener {
        func()
        return this
    }
}