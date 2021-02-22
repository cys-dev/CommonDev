package com.cys.common.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import androidx.core.content.ContextCompat
import com.cys.common.R
import com.cys.common.databinding.DialogChooseBinding
import com.cys.common.extends.dp2Px
import com.cys.common.utils.ShapeUtils

@SuppressLint("InflateParams")
class ChooseDialog(context: Context) : MessageDialog(context) {

    var chooseItems = ArrayList<String>()
    var chooseIndex = 0
    var chooseTextColor = ContextCompat.getColor(context, R.color.black_60)
    var chooseMarkColor = ContextCompat.getColor(context, R.color.blue_90)
    var chooseBackgroundColor = Color.TRANSPARENT
    var choiceMode = ListView.CHOICE_MODE_SINGLE

    val chooseBinding = DialogChooseBinding.inflate(layoutInflater)

    override fun initContentView() {
        initListView()
    }

    private fun initListView() {
        with(chooseBinding) {
            val adapter = ChooseAdapter(context, 0, chooseItems)
            dialogChooseListView.adapter = adapter
            dialogChooseListView.choiceMode = choiceMode
            dialogChooseListView.divider = null
            addContentView(chooseBinding.root)
        }
    }

    override fun initListener() {
        super.initListener()
        with(chooseBinding) {
            dialogChooseListView.setOnItemClickListener { _, _, position, _ ->
                chooseIndex = position
                chooseItems
            }
        }
    }

    inner class ChooseAdapter(context: Context, res: Int, private val list: List<String>) :
        ArrayAdapter<String>(context, res, list) {

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): String {
            return list[position]
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View
            val holder: ViewHolder
            if (convertView == null) {
                val checkedTextView = CheckedTextView(context)
                checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_mark)
                checkedTextView.checkMarkTintList = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(chooseMarkColor, Color.TRANSPARENT)
                )
                val radius = 4.dp2Px()
                val normalDrawable = ShapeUtils.getRadiusShape(radius, Color.TRANSPARENT)
                val pressedDrawable = ShapeUtils.getRadiusShape(radius, chooseBackgroundColor)
                checkedTextView.background = ShapeUtils.getColorStateListDrawable(
                    normalDrawable,
                    pressedDrawable,
                    android.R.attr.state_checked
                )
                checkedTextView.textSize = 16F
                checkedTextView.gravity = Gravity.CENTER_VERTICAL
                checkedTextView.setTextColor(chooseTextColor)

                val padding = 5.dp2Px()
                val paddingTop = 8.dp2Px()
                checkedTextView.setPadding(padding, paddingTop, padding, paddingTop)

                view = checkedTextView
                holder = ViewHolder()
                holder.checkedTextView = view
                view.setTag(holder)
            } else {
                view = convertView
                holder = view.tag as ViewHolder
            }
            view.post {
                holder.checkedTextView?.text = getItem(position)
                holder.checkedTextView?.isChecked = position == chooseIndex
            }

            return view
        }

        inner class ViewHolder {
            var checkedTextView: CheckedTextView? = null
        }
    }
}