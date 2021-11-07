package cn.yooking.genshin.view.model

import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.datasource.data.Record

/**
 * Created by yooking on 2021/9/27.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class LotteryAnalysisModel {

    val dataAllEntity: DataEntity = DataEntity()
    val dataNoviceEntity: DataEntity = DataEntity()
    val dataPermanentEntity: DataEntity = DataEntity()
    val dataUpEntity: DataEntity = DataEntity()
    val dataArmsEntity: DataEntity = DataEntity()

    fun initData(uid: String?) {
        //读取本地数据
        val allData: MutableList<Record> = arrayListOf()
        val noviceData: MutableList<Record> = arrayListOf()
        val permanentData: MutableList<Record> = arrayListOf()
        val upData: MutableList<Record> = arrayListOf()
        val armsData: MutableList<Record> = arrayListOf()

        if (uid != null && uid.isNotEmpty()) {
            noviceData.addAll(SQLiteHelper.instance.findRecord(uid, "100"))
            permanentData.addAll(SQLiteHelper.instance.findRecord(uid, "200"))
            upData.addAll(SQLiteHelper.instance.findRecord(uid, "301"))
            armsData.addAll(SQLiteHelper.instance.findRecord(uid, "302"))

            allData.addAll(noviceData)
            allData.addAll(permanentData)
            allData.addAll(upData)
            allData.addAll(armsData)
        }

        dataAllEntity.setData(allData)
        dataNoviceEntity.setData(noviceData)
        dataPermanentEntity.setData(permanentData)
        dataUpEntity.setData(upData)
        dataArmsEntity.setData(armsData)
    }


    class DataEntity {

        private var size: Int = 0

        private var stars3 = 0

        private var stars4: Int = 0
        private var stars4Arms: Int = 0
        private var stars4Role: Int = 0

        private var stars5: Int = 0
        private var stars5Arms: Int = 0
        private var stars5Role: Int = 0

        private var lastStars5Index: Int = 0

        private lateinit var lastStars5Name: String

        fun setData(data: MutableList<Record>) {
            size = data.size

            countStarsNum(data)
            countLastStars5Index(data)

            lastStars5Name = if(data.size>0) {
                val uid = data[0].uid
                val gachaType = data[0].gacha_type
                SQLiteHelper.instance.findLastStars5Name(uid, gachaType)
            }else{
                ""
            }
        }

        fun getSize(): Int {
            return size
        }

        fun getStars3(): Int {
            return stars3
        }

        fun getStars4(): Int {
            return stars4
        }

        fun getStars4Role(): Int {
            return stars4Role
        }

        fun getStars4Arms(): Int {
            return stars4Arms
        }

        fun getStars5(): Int {
            return stars5
        }

        fun getStars5Role(): Int {
            return stars5Role
        }

        fun getStars5Arms(): Int {
            return stars5Arms
        }

        fun getLastStars5Index(): Int {
            return lastStars5Index
        }

        fun getLastStars5Name(): String {
            return lastStars5Name
        }

        /**
         * 计算各星级抽中次数
         */
        private fun countStarsNum(data: MutableList<Record>) {
            for (datum in data) {
                when (datum.rank_type) {
                    "4" -> {
                        if (datum.item_type == "武器") stars4Arms++
                        else stars4Role++
                    }
                    "5" -> {
                        if (datum.item_type == "武器") stars5Arms++
                        else stars5Role++
                    }
                    else -> stars3++
                }
            }

            stars4 = stars4Arms + stars4Role
            stars5 = stars5Arms + stars5Role
        }

        /**
         * 计算本轮保底抽卡次数
         */
        private fun countLastStars5Index(data: MutableList<Record>) {
            for (i in data.size - 1 downTo 0) {
                val record = data[i]
                if (record.rank_type == "5") {
                    break
                } else {
                    lastStars5Index++
                }
            }
        }
    }
}