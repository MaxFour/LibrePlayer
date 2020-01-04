package com.maxfour.libreplayer.views

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.adapter.base.MediaEntryViewHolder
import com.maxfour.libreplayer.util.PlayerUtil

class MetalRecyclerViewPager : RecyclerView {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private var itemMargin: Int = 0

    fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MetalRecyclerViewPager, 0, 0)
        itemMargin = typedArray.getDimension(R.styleable.MetalRecyclerViewPager_itemMargin, 0f).toInt()
        typedArray.recycle()

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(this)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter is MetalAdapter) {
            adapter.setItemMargin(itemMargin)
            adapter.updateDisplayMetrics()
        } else {
            throw IllegalArgumentException("Only MetalAdapter is allowed here")
        }
        super.setAdapter(adapter)
    }

    abstract class MetalAdapter<VH : MetalViewHolder>(@NonNull val displayMetrics: DisplayMetrics) : RecyclerView.Adapter<VH>() {
        private var itemMargin: Int = 0
        private var itemWidth: Int = 0

        fun setItemMargin(itemMargin: Int) {
            this.itemMargin = itemMargin
        }

        fun updateDisplayMetrics() {
            itemWidth = if (PlayerUtil.isTablet()) {
                displayMetrics.widthPixels / 2 - itemMargin * 3
            } else if (PlayerUtil.isLandscape()) {
                ((displayMetrics.widthPixels / 2.5f) - itemMargin).toInt()
            } else {
                displayMetrics.widthPixels - itemMargin
            }
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val currentItemWidth = itemWidth
            if (position == 0) {
                //currentItemWidth += itemMargin;
                holder.rootLayout.setPadding(0, 0, 0, 0);
            } else if (position == itemCount - 1) {
                //currentItemWidth += itemMargin;
                holder.rootLayout.setPadding(0, 0, 0, 0);
            }

            val height = holder.rootLayout.layoutParams.height
            holder.rootLayout.layoutParams = ViewGroup.LayoutParams(currentItemWidth, height)
        }
    }

    abstract class MetalViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        var rootLayout: ViewGroup = itemView.findViewById(R.id.root_layout)
    }
}
