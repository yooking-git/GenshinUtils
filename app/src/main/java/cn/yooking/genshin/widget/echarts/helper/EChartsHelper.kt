package cn.yooking.genshin.widget.echarts.helper

import cn.yooking.genshin.utils.BigDecimalUtil
import cn.yooking.genshin.widget.echarts.entity.*
import com.alibaba.fastjson.JSON
import kotlin.math.min


/**
 * Created by yooking on 2021/9/23.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class EChartsHelper {
    companion object {
        fun getPieSeries(
            name: String = "default",
            titles: Array<String>,
            values: IntArray
        ): Series {
            var size: Int = min(titles.size, values.size)
            if (size < 0) {
                size = 0
            }
            val type = "pie"
            val radius = arrayOf("5%", "40%")
            val center = arrayOf("45%", "60%")
            val roseType = ""
            val data: MutableList<Series.Data> = arrayListOf();

            for (i in 0 until size) {
                val title = titles[i]
                val value = values[i]
                val itemStyle = Series.Data.ItemStyle()
                when (title.substring(0,2)) {
                    "5星" -> itemStyle.color = "#FF9224"
                    "4星" -> itemStyle.color = "#4B0091"
                    "3星" -> itemStyle.color = "#9D9D9D"
                }
                val item = Series.Data(title, value,itemStyle)
                data.add(item)
            }

            return Series(
                type, name, 10, radius, center, roseType,
                Series.ItemStyle(), Series.Label(),
                data
            )
        }

        fun getOptionString(titleName: String = "", vararg series: Series): String {
            val dataList: MutableList<Series> = arrayListOf()
            dataList.addAll(series)

            val title = Title()
            if (titleName.isNotEmpty()) {
                title.text = titleName
                title.show = true
            }
            val option = Option(title, Legend("bottom", "right", "vertical",selected = mapOf("3星" to false)), Toolbox(), dataList)
            return JSON.toJSONString(option)
        }
    }
}