package cn.yooking.genshin.utils.sp

import android.content.Context
import android.content.SharedPreferences
import com.alibaba.fastjson.JSON

/**
 * Created by yooking on 2022/4/25.
 * Copyright (c) 2022 yooking. All rights reserved.
 */
class HeaderSpUtil {
    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HeaderSpUtil()
        }
    }

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    private fun getSharedPreference(): SharedPreferences {
        return context.getSharedPreferences("GENSHIN_SP_HEADER", Context.MODE_PRIVATE)
    }

    fun addHeader(name: String, entity: HeaderEntity) {
        val edit = getSharedPreference().edit()
        edit.putString(name, JSON.toJSONString(entity))
        edit.apply()
    }

    fun removeHeader(name: String) {
        val edit = getSharedPreference().edit()
        edit.remove(name)
        edit.apply()
    }

    fun getHeader(name: String): String {
        return getSharedPreference().getString(name, "") ?: ""
    }

    fun findHeaderMap(): MutableMap<String, String> {
        return getSharedPreference().all as MutableMap<String, String>
    }

    fun findAllHeader(): MutableList<HeaderEntity> {
        val mutableMap = getSharedPreference().all as MutableMap<String, String>

        val data: MutableList<HeaderEntity> = arrayListOf()

        mutableMap.forEach {
            val parse: HeaderEntity = JSON.parseObject(it.value, HeaderEntity::class.java)
            data.add(parse)
        }

        data.sortBy {
            it.type
        }

        return data
    }

    data class HeaderEntity(
        var name: String,
        var path: String,
        var nickname: String = "",
        var type: Int = 0 // -1是添加 0是角色 1是武器
    ){
        constructor():this("","")
    }
}