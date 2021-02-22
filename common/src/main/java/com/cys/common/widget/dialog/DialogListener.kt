package com.cys.common.widget.dialog

private typealias OnConfirm = () -> Unit
private typealias OnCancel = () -> Unit

class DialogListener {

    var onConfirm: OnConfirm? = null
    var onCancel: OnCancel? = null

    fun confirm() {
        onConfirm?.invoke()
    }

    fun cancel() {
        onCancel?.invoke()
    }

    inline fun register(func: DialogListener.() -> Unit): DialogListener {
        func()
        return this
    }
}