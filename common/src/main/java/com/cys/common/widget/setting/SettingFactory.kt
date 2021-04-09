package com.cys.common.widget.setting

import androidx.annotation.ColorInt
import com.cys.common.const.Colors
import com.cys.common.utils.JsonUtils
import java.util.*

typealias SettingItemType = Int

const val SETTING_GROUP_TITLE: SettingItemType = 100
const val SETTING_SWITCH: SettingItemType = 0
const val SETTING_CHOOSE: SettingItemType = 1
const val SETTING_SLIDER: SettingItemType = 2
const val SETTING_ENTRANCE: SettingItemType = 3
const val SETTING_COLOR: SettingItemType = 4
const val SETTING_INPUT: SettingItemType = 5

class SettingFactory {

    val itemProducts = linkedMapOf<Int, SettingItem>()
    var noticeIndex = -1
    var title: String = ""
    var divider: Boolean = false

    @ColorInt
    var themeColor = Colors.BLUE

    data class SettingItem(
        val type: SettingItemType,
        val key: String,
        val title: String,
        var summary: String = "",

        val switchChecked: Boolean = false,

        val chooseItems: ArrayList<String> = arrayListOf(),
        val chooseDefault: String = "",

        val sliderFrom: Float = 0f,
        val sliderTo: Float = 100f,
        val sliderDefault: Float = 50f,
        val sliderScale: Int = 0,

        val entrance: SettingFactory? = null,
        val showEntranceIcon: Boolean = true,

        val color: Int = Colors.WHITE,

        val groupDivider: Boolean = true
    )

    fun addGroupTitle(title: String, groupDivider: Boolean = true) : SettingFactory{
        itemProducts[itemProducts.size] = SettingItem(
            SETTING_GROUP_TITLE,
            UUID.randomUUID().toString(), title, groupDivider = groupDivider
        )
        return this
    }

    fun addSwitch(
        key: String,
        title: String,
        checked: Boolean,
        summary: String = ""
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            SETTING_SWITCH,
            key, title, summary, checked
        )
        return this
    }

    fun addChoose(
        key: String,
        title: String,
        items: ArrayList<String>,
        default: String
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            SETTING_CHOOSE,
            key, title,
            chooseItems = items,
            chooseDefault = default
        )
        return this
    }

    fun addSlider(
        key: String,
        title: String,
        from: Float,
        to: Float,
        default: Float,
        scale: Int = 0
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            SETTING_SLIDER,
            key, title,
            sliderFrom = from,
            sliderTo = to,
            sliderDefault = default,
            sliderScale = scale
        )
        return this
    }

    fun addEntrance(title: String, factory: SettingFactory, showIcon: Boolean = true) {
        itemProducts[itemProducts.size] = SettingItem(
            SETTING_ENTRANCE, UUID.randomUUID().toString(), title,
            entrance = factory, showEntranceIcon = showIcon
        )
    }

    fun addColorPicker(
        key: String,
        title: String,
        default: Int
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            SETTING_COLOR,
            key, title, color = default
        )
        return this
    }

    fun addTextInput(
        key: String,
        title: String,
        default: String
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            SETTING_INPUT,
            key, title, summary = default
        )
        return this
    }

    companion object {
        fun toJson(factory: SettingFactory): String {
            return JsonUtils.gson.toJson(factory)
        }

        fun fromJson(json: String): SettingFactory? {
            var factory: SettingFactory? = null
            try {
                factory = JsonUtils.gson.fromJson(json, SettingFactory::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return factory
        }
    }
}