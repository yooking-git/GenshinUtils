package cn.yooking.genshin.adapter

import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Created by yooking on 2021/9/14.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
abstract class DraggableAdapter<T, VH : BaseViewHolder>(
    @LayoutRes private val layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, VH>(layoutResId, data), DraggableModule