package cn.yooking.genshin.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间转换
 * Created by yooking on 2021/9/24.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class DateUtil {

    companion object {
        fun str2Long(strDate: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
            return try {
                str2Date(strDate, pattern)?.time ?: -1L
            } catch (e: Exception) {
                -1L
            }
        }

        fun date2Str(date: Date, pattern: String): String {
            return try {
                val dateFormat = SimpleDateFormat(pattern, Locale.CHINA)
                dateFormat.format(date)
            } catch (e: Exception) {
                ""
            }
        }

        fun formatStr(
            strDate: String,
            parse: String = "yyyyMMddHHmmss",
            pattern: String = "yyyy-MM-dd HH:mm:ss"
        ): String {
            return try {
                val date = str2Date(strDate, parse)!!
                date2Str(date, pattern)
            } catch (e: Exception) {
                ""
            }
        }

        fun str2Date(strDate: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): Date? {
            return try {
                val dateFormat = SimpleDateFormat(pattern, Locale.CHINA)
                dateFormat.parse(strDate)!!
            } catch (e: Exception) {
                null
            }
        }
    }


}