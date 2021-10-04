package cn.yooking.genshin.datasource.data

import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.utils.DateUtil
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.util.*

/**
 * Created by yooking on 2021/9/23.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class User(
    @Column(unique = true, defaultValue = "unknown")
    var uid: String,
    var nickname: String,
    var sort: Int = 10000,
    var recordList: MutableList<Record>,
    var lastDate: String
) : LitePalSupport() {

    constructor() : this("", "", 10000, arrayListOf(), DateUtil.date2Str(Date()))

    fun findRecord(type: String): List<Record> {
        return SQLiteHelper.instance.findRecord(uid, type)
    }

    fun findRecord(): List<Record> {
        return SQLiteHelper.instance.findAllRecord(uid)
    }
}