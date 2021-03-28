package com.cys.common.demo

import android.app.Application
import com.cys.common.CommonManager

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        CommonManager.init(this)
    }

}