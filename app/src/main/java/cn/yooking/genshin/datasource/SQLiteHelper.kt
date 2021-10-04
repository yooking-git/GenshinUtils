package cn.yooking.genshin.datasource

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import cn.yooking.genshin.datasource.data.Record
import cn.yooking.genshin.datasource.data.User
import cn.yooking.genshin.utils.DateUtil
import org.litepal.LitePal
import org.litepal.LitePal.getDatabase
import org.litepal.extension.find
import org.litepal.extension.findAll
import org.litepal.extension.findFirst
import java.util.*


/**
 * Created by yooking on 2021/9/23.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class SQLiteHelper {
    companion object {
        val instance: SQLiteHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SQLiteHelper()
        }
    }

    var db: SQLiteDatabase = getDatabase()

    /**
     * @return IntArray[0]=导入数据量 IntArray[1]=重复数据量
     */
    fun save(data: MutableList<Record>): IntArray {
        var saveCount = 0
        var repeatCount = 0
        if (data.size == 0) return intArrayOf(saveCount, repeatCount)

        val uid = data[0].uid

        val allUser = LitePal.findAll<User>()
        var hasUser = false

        var user: User? = null
        for (item in allUser) {
            if (item.uid == uid) {
                hasUser = true
                user = item
                break
            }
        }

        if (!hasUser) {
            user = User()
            user.uid = uid
        }

        var lastDate: Date = DateUtil.str2Date("1990-01-01 00:00:00")!!
        for (datum in data) {
            val count = LitePal.where("cardId=?", datum.cardId).count(Record::class.java)
            if (count == 0) {
                datum.save()
                saveCount++
            } else {
                repeatCount++
            }
            val date = DateUtil.str2Date(datum.time)
            if (date?.after(lastDate) == true) {
                lastDate = date
            }
        }

        user?.lastDate = DateUtil.date2Str(lastDate)
        user?.save()
        return intArrayOf(saveCount, repeatCount)
    }

    /**
     * 批量改变用户顺序
     */
    fun changeUserSort(vararg uidArr: String) {
        for (i in uidArr.indices) {
            updateUserSort(uidArr[i], i)
        }
    }

    /**
     * 改变单个用户的sort值
     */
    private fun updateUserSort(uid: String, sort: Int) {
        val values = ContentValues()
        values.put("sort", sort)
        LitePal.updateAll(User::class.java, values, "uid=?", uid)
    }

    /**
     * 改变用户的昵称
     */
    fun updateNickname(uid: String, nickname: String) {
        val values = ContentValues()
        values.put("nickname", nickname)
        LitePal.updateAll(User::class.java, values, "uid=?", uid)
    }

    /**
     * 修改最后一次抽卡时间
     */
    fun updateLastDate() {

    }

    /**
     * 根据uid获取单个用户对象
     * @param uid
     */
    fun findUser(uid: String): User? {
        return LitePal.where("uid=${uid}").findFirst()
    }

    /**
     * 获取用户列表
     */
    fun findAllUser(): MutableList<User> {
        val allUser = LitePal.findAll<User>()
        allUser.sortBy { it.sort }
        return allUser
    }

    /**
     * 获取单个用户的所有抽卡记录
     */
    fun findAllRecord(uid: String): List<Record> {
        return LitePal.where("uid=${uid}").order("cardId").find()
    }

    /**
     * 获取单个用户的所有抽卡记录（去除重复、并计算重复次数）
     * @param uid 用户id
     * @param type 抽卡类型 与gacha_type一致
     */
    fun findAllRecordCount(uid: String, type: String = ""): List<Map<String, String>> {
        val where = "where uid=${uid} " + if (type.isNotEmpty()) "and gacha_type=${type} " else ""
        val sql = "select " +
                "*,count(*) as count " +
                "from record " +
                where +
                "group by name " +
                "order by rank_type desc,item_type desc,count(*) desc"

        val cursor = LitePal.findBySQL(sql)
        val list = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val map = mutableMapOf<String, String>()
            val columnNames = cursor.columnNames
            for (columnName in columnNames) {
                val value = cursor.getString(cursor.getColumnIndex(columnName)) ?: ""
                map[columnName] = value
            }
            list.add(map)
        }
        return list
    }

    /**
     * 获取单个用户的某个抽卡类型的抽卡记录（不去重）
     * @param type 抽卡类型 与gacha_type一致
     */
    fun findRecord(uid: String, type: String): List<Record> {
        return LitePal.where("uid=${uid} and gacha_type='${type}'").order("cardId").find()
    }

    /**
     * 获取距离最近一次5星的次数
     */
    fun findLastStars5(uid: String, type: String): Int {
        val allRecord = findRecord(uid, type)
        var stars5 = 0
        for (i in (allRecord.size - 1) downTo 0) {
            if (allRecord[i].rank_type == "5") {
                break
            } else {
                stars5++
            }
        }
        return stars5
    }

    /**
     * 删除用户、但不删除数据
     */
    fun deleteUserWithoutRecord(uid: String) {
        //1.删除用户
        val findFirst = LitePal.where("uid=${uid}").findFirst<User>()
        findFirst?.delete()
    }
}