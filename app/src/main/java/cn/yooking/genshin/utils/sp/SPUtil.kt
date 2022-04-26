package cn.yooking.genshin.utils.sp

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by yooking on 2022/4/25.
 * Copyright (c) 2022 yooking. All rights reserved.
 */
class SPUtil private constructor() {

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SPUtil()
        }
    }

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    private fun getSharedPreference(): SharedPreferences {
        return context.getSharedPreferences("GENSHIN_SP", Context.MODE_PRIVATE)
    }

    fun put(key: String, value: Any) {
        val sp = getSharedPreference()
        val edit = sp.edit()
        when (value) {
            is String -> {
                edit.putString(key, value)
            }
            is Long -> {
                edit.putLong(key, value)
            }
            is Float -> {
                edit.putFloat(key, value)
            }
            is Int -> {
                edit.putInt(key, value)
            }
            is Set<*> -> {
                var isAllString = true
                value.forEach {
                    if (it !is String) {
                        isAllString = false
                        return@forEach
                    }
                }
                if (isAllString) {
                    edit.putStringSet(key, value as Set<String>)
                }
            }
        }
        edit.apply()
    }

    fun <T> get(key: String, default: T): T? {
        val sp = getSharedPreference()
        val value: Any? = when (default) {
            is String -> {
                sp.getString(key, default)
            }
            is Long -> {
                sp.getLong(key, default)
            }
            is Float -> {
                sp.getFloat(key, default)
            }
            is Int -> {
                sp.getInt(key, default)
            }
            else -> null
        }
        return value as T
    }
}