package cn.yooking.genshin.utils.okhttp

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by yooking on 2021/9/22.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
abstract class StringCallback : Callback {
    abstract fun onStart()
    abstract fun onResponse(code: Int, response: String)
    abstract fun onError(code: Int, message: String)
    abstract fun onEnd()

    override fun onFailure(call: Call, e: IOException) {
        onError(-1, "IO异常:${e.message}")
    }

    override fun onResponse(call: Call, response: Response) {
        try {
            val code = response.code
            if (response.isSuccessful) {
                val body = response.body
                if (body == null) {
                    onError(code, response.message)
                    onEnd()
                    return
                }
                onResponse(code, body.string())
                onEnd()
                return
            }
            onError(code, response.message)
            onEnd()
        } catch (e: Exception) {
            onError(-1, e.message ?: "发生未知错误")
            onEnd()
        }
    }
}