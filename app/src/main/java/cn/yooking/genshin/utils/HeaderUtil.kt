package cn.yooking.genshin.utils

import cn.yooking.genshin.R
import cn.yooking.genshin.utils.sp.HeaderSpUtil
import com.alibaba.fastjson.JSON

/**
 * Created by yooking on 2022/4/25.
 * Copyright (c) 2022 yooking. All rights reserved.
 */
class HeaderUtil {

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HeaderUtil()
        }
    }

    private lateinit var headerMap: MutableMap<String, String>

    fun init() {
        headerMap = HeaderSpUtil.instance.findHeaderMap()
    }

    fun getHeaderEntity(name: String): HeaderSpUtil.HeaderEntity? {
        val json = headerMap[name] ?: return null
        return JSON.parseObject(json, HeaderSpUtil.HeaderEntity::class.java)
    }

}