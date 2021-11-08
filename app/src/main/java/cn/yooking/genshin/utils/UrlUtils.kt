package cn.yooking.genshin.utils

import java.lang.StringBuilder

/**
 * Created by yooking on 2021/11/8.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class UrlUtils {

    companion object {
        private fun substringUrl(url: String): String {
            //1.截取url
            return if (url.contains("?")) {
                url.substring(
                    url.indexOf("?") + 1,
                    if (url.contains("#")) url.indexOf("#") else url.length
                )
            } else
                ""
        }

        fun getUrlParams(url: String): MutableMap<String, String> {
            val urlParams = substringUrl(url)
            if (urlParams.isEmpty()) return HashMap()
            val split = urlParams.split("&")
            val params = mutableMapOf<String, String>()
            for (item in split) {
                val itemArr = item.split("=")
                if (itemArr.size == 2) {
                    val key = itemArr[0]
                    val value = itemArr[1]
                    params[key] = value
                } else continue
            }
            return params
        }

        fun params2String(params:Map<String,String>):String{
            val urlParams = StringBuilder("")
            for (entry in params.entries) {
                val key = entry.key
                val value = entry.value
                if(urlParams.isNotEmpty()){
                    urlParams.append("&")
                }
                urlParams.append(key)
                    .append("=")
                    .append(value)
            }
            return urlParams.toString()
        }
    }
}