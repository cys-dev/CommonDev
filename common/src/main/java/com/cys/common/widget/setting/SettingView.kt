package com.cys.common.widget.setting

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cys.common.widget.setting.view.*
import com.drakeet.multitype.MultiTypeAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer

class SettingView : RecyclerView, SettingCallback {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val multiTypeAdapter = MultiTypeAdapter()
    private val data = ArrayList<SettingFactory.SettingItem>()
    private var noticeTimer: Timer? = null
    private var noticeCount = 0
    var callback: SettingCallback? = null

    // must call
    fun initView(factory: SettingFactory) {
        registerViewDelegate(factory.themeColor)
        multiTypeAdapter.setHasStableIds(true)
        adapter = multiTypeAdapter
        layoutManager = LinearLayoutManager(context)
        setData(factory.itemProducts.values.toList())
        showNoticeItem(factory.noticeIndex)
    }

    fun setData(list: List<SettingFactory.SettingItem>) {
        val diffResult = DiffUtil.calculateDiff(SettingDiffUtil(list, data))
        diffResult.dispatchUpdatesTo(multiTypeAdapter)
        data.clear()
        data.addAll(list)
        multiTypeAdapter.items = list
    }

    private fun registerViewDelegate(@ColorInt themeColor: Int) {
        multiTypeAdapter.register(SettingFactory.SettingItem::class.java)
            .to(
                GroupViewDelegate(this, themeColor),
                SwitchViewDelegate(this, themeColor),
                ChooseViewDelegate(this, themeColor),
                SliderViewDelegate(this, themeColor),
                EntranceViewDelegate(this, themeColor),
                ColorViewDelegate(this, themeColor),
                InputViewDelegate(this, themeColor)
            )
            .withKotlinClassLinker { _, item ->
                when (item.type) {
                    SETTING_GROUP_TITLE -> GroupViewDelegate::class
                    SETTING_SWITCH -> SwitchViewDelegate::class
                    SETTING_CHOOSE -> ChooseViewDelegate::class
                    SETTING_SLIDER -> SliderViewDelegate::class
                    SETTING_ENTRANCE -> EntranceViewDelegate::class
                    SETTING_COLOR -> ColorViewDelegate::class
                    SETTING_INPUT -> InputViewDelegate::class
                    else -> throw IllegalArgumentException("type error: ${item.type}")
                }
            }
    }

    fun showNotice(id: Int) {
        multiTypeAdapter.items.forEachIndexed { index, it ->
            if (it is SettingFactory.SettingItem && it.id == id) {
                showNoticeItem(index)
                return
            }
        }
    }

    private fun showNoticeItem(index: Int) {
        if (index == -1) {
            return
        }
        noticeTimer?.cancel()

        noticeTimer = fixedRateTimer("notice", false, 200, 650) {
            val itemView: View? = layoutManager?.findViewByPosition(index)
            itemView?.let {
                if (noticeCount++ < 3) {
                    it.isActivated = !it.isActivated
                } else {
                    it.isActivated = false
                    noticeTimer?.cancel()
                }
            }
        }
    }

    override fun onSwitchChanged(id: Int, checked: Boolean) {
        callback?.onSwitchChanged(id, checked)
    }

    override fun onChooseChanged(id: Int, choose: String) {
        callback?.onChooseChanged(id, choose)
    }

    override fun onSliderChanged(id: Int, value: Float) {
        callback?.onSliderChanged(id, value)
    }

    override fun onColorChanged(id: Int, color: Int) {
        callback?.onColorChanged(id, color)
    }

    override fun onEntranceClicked(id: Int) {
        callback?.onEntranceClicked(id)
    }

    override fun onInputText(id: Int, text: String) {
        callback?.onInputText(id, text)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        noticeTimer?.cancel()
    }
}