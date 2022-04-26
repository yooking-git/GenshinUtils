package cn.yooking.genshin

import android.app.Application
import cn.yooking.genshin.utils.sp.HeaderSpUtil
import cn.yooking.genshin.utils.sp.SPUtil
import org.litepal.LitePal

/**
 * Created by yooking on 2021/9/13.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class YApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LitePal.initialize(this)

        HeaderSpUtil.instance.init(this)
        SPUtil.instance.init(this)
    }
}