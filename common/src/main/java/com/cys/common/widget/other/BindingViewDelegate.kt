package com.cys.common.widget.other

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.drakeet.multitype.ItemViewDelegate

abstract class BindingViewDelegate<T, V : ViewBinding> : ItemViewDelegate<T, BindingViewDelegate.Holder<V>>() {

    abstract fun onCreateBinding(context: Context, parent: ViewGroup): V

    abstract fun onBindBinding(binding: V, item: T)


    // Override this function if you need a ViewHolder
    open fun onBindBinding(holder: Holder<V>, binding: V, item: T) {
        onBindBinding(binding, item)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): Holder<V> {
        return Holder(onCreateBinding(context, parent))
    }

    override fun onBindViewHolder(holder: Holder<V>, item: T) = onBindBinding(holder, holder.binding, item)

    class Holder<V : ViewBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)

    /**
     * Warning: this property can only get the correct value in an item root view.
     * @see RecyclerView.LayoutParams.getViewLayoutPosition
     */
    protected val View.layoutPosition get() = recyclerLayoutParams.viewLayoutPosition

    /**
     * Warning: this property can only get the correct value in an item root view.
     * @see RecyclerView.LayoutParams.getViewAdapterPosition
     */
    protected val View.adapterPosition get() = recyclerLayoutParams.viewAdapterPosition

    /**
     * RecyclerView will automatically convert any original type of LayoutParam into [RecyclerView.LayoutParams],
     * so for the item root view, its final LayoutParam must be [RecyclerView.LayoutParams].
     * @see RecyclerView.LayoutManager.generateLayoutParams
     */
    private val View.recyclerLayoutParams get() = layoutParams as RecyclerView.LayoutParams
}