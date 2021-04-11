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

    @ColorInt
    var themeColor = Colors.BLUE

    data class SettingItem(
        val id: Int,
        val type: SettingItemType,
        val title: String,
        val key: String = "",
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
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SettingItem) return false

            if (id != other.id) return false
            if (type != other.type) return false
            if (title != other.title) return false
            if (key != other.key) return false
            if (summary != other.summary) return false
            if (switchChecked != other.switchChecked) return false
            if (chooseItems != other.chooseItems) return false
            if (chooseDefault != other.chooseDefault) return false
            if (sliderFrom != other.sliderFrom) return false
            if (sliderTo != other.sliderTo) return false
            if (sliderDefault != other.sliderDefault) return false
            if (sliderScale != other.sliderScale) return false
            if (showEntranceIcon != other.showEntranceIcon) return false
            if (color != other.color) return false
            if (groupDivider != other.groupDivider) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + type
            result = 31 * result + title.hashCode()
            result = 31 * result + key.hashCode()
            result = 31 * result + summary.hashCode()
            result = 31 * result + switchChecked.hashCode()
            result = 31 * result + chooseItems.hashCode()
            result = 31 * result + chooseDefault.hashCode()
            result = 31 * result + sliderFrom.hashCode()
            result = 31 * result + sliderTo.hashCode()
            result = 31 * result + sliderDefault.hashCode()
            result = 31 * result + sliderScale
            result = 31 * result + showEntranceIcon.hashCode()
            result = 31 * result + color
            result = 31 * result + groupDivider.hashCode()
            return result
        }
    }

    fun addGroupTitle(id: Int, title: String, groupDivider: Boolean = true): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            id, SETTING_GROUP_TITLE,
            title, groupDivider = groupDivider
        )
        return this
    }

    fun addSwitch(
        id: Int,
        title: String,
        checked: Boolean,
        key: String = "",
        summary: String = ""
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            id, SETTING_SWITCH, title, key, summary, checked
        )
        return this
    }

    fun addChoose(
        id: Int,
        title: String,
        items: ArrayList<String>,
        default: String,
        key: String = ""
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            id, SETTING_CHOOSE, title, key,
            chooseItems = items,
            chooseDefault = default
        )
        return this
    }

    fun addSlider(
        id: Int,
        title: String,
        from: Float,
        to: Float,
        default: Float,
        scale: Int = 0,
        key: String = ""
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            id, SETTING_SLIDER,
            title, key,
            sliderFrom = from,
            sliderTo = to,
            sliderDefault = default,
            sliderScale = scale
        )
        return this
    }

    fun addEntrance(id: Int, title: String, factory: SettingFactory?, showIcon: Boolean = true) {
        itemProducts[itemProducts.size] = SettingItem(
            id, SETTING_ENTRANCE, title,
            entrance = factory, showEntranceIcon = showIcon
        )
    }

    fun addColorPicker(
        id: Int,
        title: String,
        default: Int,
        key: String = ""
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            id, SETTING_COLOR,
            title, key, color = default
        )
        return this
    }

    fun addTextInput(
        id: Int,
        title: String,
        default: String,
        key: String = ""
    ): SettingFactory {
        itemProducts[itemProducts.size] = SettingItem(
            id, SETTING_INPUT,
            title, key, summary = default
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