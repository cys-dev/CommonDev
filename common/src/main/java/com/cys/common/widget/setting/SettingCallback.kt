package com.cys.common.widget.setting

interface SettingCallback {
    fun onSwitchChanged(id: Int, checked: Boolean)
    fun onChooseChanged(id: Int, choose: String)
    fun onSliderChanged(id: Int, value: Float)
    fun onColorChanged(id: Int, color: Int)
    fun onInputText(id: Int, text: String)
    fun onEntranceClicked(id: Int)
}