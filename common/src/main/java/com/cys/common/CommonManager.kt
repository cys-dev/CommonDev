package com.cys.common

import android.content.Context
import com.tencent.mmkv.MMKV
import org.litepal.LitePal

object CommonManager {

    fun init(context: Context) {
        LitePal.initialize(context)
        MMKV.initialize(context)
    }
}