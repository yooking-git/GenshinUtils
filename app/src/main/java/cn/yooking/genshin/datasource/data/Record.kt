package cn.yooking.genshin.datasource.data

import com.alibaba.fastjson.annotation.JSONField
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

/**
 * Created by yooking on 2021/9/23.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class Record(
    @Column(nullable = false, index = true)
    var uid: String,
    var gacha_type: String,
    var item_id: String,
    var count: String,
    var time: String,
    var name: String,
    var lang: String,
    var item_type: String,
    var rank_type: String,

    @Column(nullable = false, index = true)
    var cardId: String
) : LitePalSupport() {
    //{
    //"uid": "198915592",
    //"gacha_type": "200",
    //"item_id": "",
    //"count": "1",
    //"time": "2021-09-05 16:54:49",
    //"name": "黎明神剑",
    //"lang": "zh-cn",
    //"item_type": "武器",
    //"rank_type": "3",
    //"id": "1630829160001879659"
    //}

    // fastJson 需要一个空构造函数
    constructor() : this("", "", "", "", "", "", "", "", "", "")

    // fastJson 是根据get和set方法进行数据转换的 - litePal不支持主键自定义
    public fun setId(id:String){
        cardId = id
    }

    public fun getId():String{
        return cardId
    }
}