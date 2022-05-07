package cn.yooking.genshin.view.model

import cn.yooking.genshin.R
import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.utils.BigDecimalUtil

/**
 * Created by yooking on 2022/4/24.
 * Copyright (c) 2022 yooking. All rights reserved.
 */
class LotteryAnalysisModel2(val uid: String) {

    val fortune: String //运势
    val fortuneColor: Int //运势
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

        this.fortune = getFortuneText(d)
        this.fortuneColor = getFortuneColor(d)
    }

    fun getFortuneText(fortune: Double): String {
        return when {
            fortune > 70 -> "非"
            fortune > 65 -> "衰"
            fortune > 60 -> "平"
            fortune > 55 -> "吉"
            fortune > 30 -> "欧"
            else -> "神"
        }
    }

    fun getFortuneColor(fortune: Double): Int {
        return when {
            fortune > 70 -> R.color.color_fortune_5
            fortune > 65 -> R.color.color_fortune_4
            fortune > 60 -> R.color.color_fortune_3
            fortune > 55 -> R.color.color_fortune_2
            fortune > 30 -> R.color.color_fortune_1
            else -> R.color.color_fortune_0
        }
    }

    class DataEntity(
        val name: String,
        val distanceCount: Int,
    )

    inner class InfoEntity(
        uid: String,//用户ID
        type: String,//抽卡类型
    ) {
        val fortune: String //运势
        val fortuneColor: Int //运势颜色
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
            this.fortune = getFortuneText(d)
            this.fortuneColor = getFortuneColor(d)
        }
    }
}