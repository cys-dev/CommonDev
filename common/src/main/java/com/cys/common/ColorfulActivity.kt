package com.cys.common

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cys.common.const.Colors
import com.cys.common.databinding.ActivityColorfulBinding
import com.cys.common.utils.systembar.StatusBarHelper

abstract class ColorfulActivity : AppCompatActivity() {

    lateinit var colorfulBinding: ActivityColorfulBinding

    data class ColorConfig(
        @ColorInt val actionBarBackGroundColor: Int = Colors.BLUE,
        @ColorInt val actionBarForeGroundColor: Int = Colors.WHITE,
        val title: String = "",
        val lightStatusBarIcon: Boolean = false,
        val showBackButton: Boolean = true
    )

    open fun getColorConfig(): ColorConfig {
        return ColorConfig()
    }

    abstract fun getContentView(): View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colorfulBinding = ActivityColorfulBinding.inflate(layoutInflater)
        colorfulBinding.contentContainer.addView(
            getContentView(),
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setContentView(colorfulBinding.root)
        setSupportActionBar(colorfulBinding.toolBar)
        setColorConfig(getColorConfig())
    }

    fun setColorConfig(config: ColorConfig) = with(colorfulBinding) {
        colorfulBinding.toolBar.title = config.title
        colorfulBinding.toolBar.setBackgroundColor(config.actionBarBackGroundColor)
        colorfulBinding.toolBar.setTitleTextColor(config.actionBarForeGroundColor)
        supportActionBar?.setDisplayHomeAsUpEnabled(config.showBackButton)

        if (config.showBackButton) {
            val drawable = ContextCompat.getDrawable(this@ColorfulActivity, R.drawable.ic_back)
            drawable?.setTint(config.actionBarForeGroundColor)
            colorfulBinding.toolBar.setNavigationIcon(R.drawable.ic_back)
        }

        StatusBarHelper.translucent(window)
        if (config.lightStatusBarIcon) {
            StatusBarHelper.setStatusBarLightMode(this@ColorfulActivity)
        } else {
            StatusBarHelper.setStatusBarDarkMode(this@ColorfulActivity)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeAsUp) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}