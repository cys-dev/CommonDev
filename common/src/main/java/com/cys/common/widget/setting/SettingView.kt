package com.cys.common.widget.setting

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cys.common.widget.setting.view.*
import com.drakeet.multitype.MultiTypeAdapter
import java.util.*
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
    private var noticeTimer: Timer? = null
    private var noticeCount = 0
    var callback: SettingCallback? = null

    // must call
    fun initView(factory: SettingFactory) {
        registerViewDelegate(factory.themeColor)
        multiTypeAdapter.setHasStableIds(true)
        adapter = multiTypeAdapter
        layoutManager = LinearLayoutManager(context)
        multiTypeAdapter.items = factory.itemProducts.values.toList()
        multiTypeAdapter.notifyDataSetChanged()
        showNoticeItem(factory.noticeIndex)
    }

    private fun registerViewDelegate(@ColorInt themeColor: Int) {
        multiTypeAdapter.register(SettingFactory.SettingItem::class.java)
            .to(
                SwitchViewDelegate(this, themeColor),
                ChooseViewDelegate(this, themeColor),
                SliderViewDelegate(this, themeColor),
                EntranceViewDelegate(this, themeColor),
                GroupViewDelegate(this, themeColor)
            )
            .withKotlinClassLinker { _, item ->
                when (item.type) {
                    SETTING_SWITCH -> SwitchViewDelegate::class
                    SETTING_CHOOSE -> ChooseViewDelegate::class
                    SETTING_SLIDER -> SliderViewDelegate::class
                    SETTING_ENTRANCE -> EntranceViewDelegate::class
                    SETTING_GROUP_TITLE -> GroupViewDelegate::class
                    else -> throw IllegalArgumentException("type error: ${item.type}")
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

    override fun onSettingChanged(key: String) {
        callback?.onSettingChanged(key)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        noticeTimer?.cancel()
    }
}