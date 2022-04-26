package cn.yooking.genshin.view.model

import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.utils.BigDecimalUtil

/**
 * Created by yooking on 2022/4/24.
 * Copyright (c) 2022 yooking. All rights reserved.
 */
class LotteryAnalysisModel2(val uid: String) {

    val fortune: String //运势
    val starts5Times: String //出5星次数
    val averageTimes: String //平均5星次数
    val awardTimes: String //总抽卡次数

    val upData: InfoEntity = InfoEntity(uid, "301")
    val armsData: InfoEntity = InfoEntity(uid, "302")
    val permanentData: InfoEntity = InfoEntity(uid, "200")

    init {

        // 统计五星次数
        val starts5Times = BigDecimalUtil.add(
            upData.starts5Times,
            armsData.starts5Times,
            permanentData.starts5Times
        )

        // 统计抽卡数
        val awardTimes = BigDecimalUtil.add(
            upData.awardTimes,
            armsData.awardTimes,
            permanentData.awardTimes
        )

        this.starts5Times = "$starts5Times"
        this.awardTimes = "$awardTimes"
        this.averageTimes = BigDecimalUtil.divide(awardTimes, starts5Times)

        val d = BigDecimalUtil.divide(awardTimes, starts5Times).toDouble()
        this.fortune = when {
            d > 70 -> "非"
            d > 65 -> "凶"
            d > 60 -> "平"
            d > 55 -> "吉"
            d > 30 -> "欧"
            else -> "神"
        }
    }

    class DataEntity(
        val name: String,
        val distanceCount: Int,
    )

    class InfoEntity(
        uid: String,//用户ID
        type: String,//抽卡类型
    ) {
        val fortune: String //运势
        val withoutTimes: String //未出5星次数
        val starts5Times: String //出5星次数
        val averageTimes: String //平均5星次数
        val awardTimes: String //总抽卡次数
        val legendData: MutableList<DataEntity> = arrayListOf()

        init {
            val recordData = SQLiteHelper.instance.findRecord(uid, type)

            val awardTimes = recordData.size
            var starts5Times = 0

            var last5StartsIndex = 0
            recordData.forEachIndexed { index, record ->
                if (record.rank_type == "5") {
                    legendData.add(DataEntity(record.name, index - last5StartsIndex))
                    last5StartsIndex = index
                    ++starts5Times
                }
            }

            this.awardTimes = "$awardTimes"
            this.withoutTimes = "${awardTimes - last5StartsIndex - 1}"
            this.starts5Times = "$starts5Times"
            this.averageTimes = BigDecimalUtil.divide(awardTimes, starts5Times)

            val d = BigDecimalUtil.divide(awardTimes, starts5Times).toDouble()
            this.fortune = when {
                d > 70 -> "非"
                d > 65 -> "凶"
                d > 60 -> "平"
                d > 55 -> "吉"
                d > 30 -> "欧"
                else -> "神"
            }
        }
    }
}