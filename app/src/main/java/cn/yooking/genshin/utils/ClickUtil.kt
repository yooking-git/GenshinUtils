package cn.yooking.genshin.utils

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import java.util.*

/**
 * 按钮点击工具类
 * Created by yooking on 2021/10/4.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class ClickUtil {
    //初始化时间
    private val clickDuration = 1000L
    private var lastTime = DateUtil.str2Long("1990-01-01 00:00:00")
    private val viewList: MutableList<View> = arrayListOf()

    fun bind(view: View) {
        if (!isBind(view)) {
            viewList.add(view)
        }
    }

    private fun isBind(view: View): Boolean {
        return viewList.contains(view)
    }

    fun clickEnable(): Boolean {
        val clickTime = Date().time
        val clickEnable = clickTime - lastTime > clickDuration
        if (clickEnable) {
            lastTime = clickTime
        }
        return clickEnable
    }
}

abstract class NoMultipleItemClickListener : OnItemClickListener {
    private val clickUtil: ClickUtil = ClickUtil()
    fun clickEnable(view: View): Boolean {
        clickUtil.bind(view)
        return clickUtil.clickEnable()
    }
}

abstract class NoMultipleItemChildClickListener:OnItemChildClickListener{
    private val clickUtil: ClickUtil = ClickUtil()
    fun clickEnable(view: View): Boolean {
        clickUtil.bind(view)
        return clickUtil.clickEnable()
    }
}