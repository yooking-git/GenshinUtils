package cn.yooking.genshin.utils.okhttp

import android.util.Log
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Created by yooking on 2021/9/22.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class OkhttpUtil {
    companion object {
        private const val TAG: String = "okhttpLog"//日志抬头
        private const val READ_TIMEOUT: Long = 30//读取超时
        private const val WRITE_TIMEOUT: Long = 30//写入超时
        private const val CONNECT_TIMEOUT: Long = 30//连接超时


        val instance: OkhttpUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OkhttpUtil()
        }

    }

    private val okHttpClient: OkHttpClient

    init {
        //引入log日志
        val interceptor = HttpLoggingInterceptor(
            object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d(TAG, message)
                }
            }
        )
        interceptor.level = HttpLoggingInterceptor.Level.BODY//日志等级
        okHttpClient = OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .retryOnConnectionFailure(true)//断线重连
            .addInterceptor(interceptor)//添加拦截器
            .build()
    }


    fun get(url: String, callback: StringCallback) {
        try {
            callback.onStart()
            val request = Request.Builder().url(url).build()
            val call: Call = okHttpClient.newCall(request)
            call.enqueue(callback)
        } catch (e: Exception) {
            callback.onError(-1, e.message ?: "发生未知错误")
            callback.onEnd()
        }
    }
}