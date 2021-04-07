package com.cys.common.widget.setting

interface SettingCallback {
    fun onSwitchChanged(key: String, checked: Boolean)
    fun onChooseChanged(key: String, choose: String)
    fun onSliderChanged(key: String, value: Float)
    fun onColorChanged(key: String, color: Int)
    fun onInputText(key: String, text: String)
    fun onEntranceClicked(key: String)
}