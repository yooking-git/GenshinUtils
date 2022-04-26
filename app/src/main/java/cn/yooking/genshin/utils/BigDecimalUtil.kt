package cn.yooking.genshin.utils

import java.math.BigDecimal

/**
 * 科学计算法
 * Created by yooking on 2021/9/24.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class BigDecimalUtil {
    companion object {

        fun percentage(numerator: Int, denominator: Int): String {
            if (denominator == 0) return "0%"
            val b1 = BigDecimal(numerator).multiply(BigDecimal(100))
            val b2 = BigDecimal(denominator)
            return "${b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).toDouble()}%"
        }

        fun divide(numerator: Int, denominator: Int): String {
            if (denominator == 0) return "0"
            val b1 = BigDecimal(numerator)
            val b2 = BigDecimal(denominator)
            return "${b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).toDouble()}"
        }

        fun add(vararg nums: String): Int {
            if (nums.isEmpty()) return 0
            if (nums.size < 2) return BigDecimal(nums[0]).toInt()

            var b0 = BigDecimal("0")
            nums.forEach {
                b0 = b0.add(BigDecimal(it))
            }
            return b0.toInt()
        }
    }
}