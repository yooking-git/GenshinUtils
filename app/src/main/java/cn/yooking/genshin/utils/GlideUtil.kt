package cn.yooking.genshin.utils

import android.content.Context
import android.widget.ImageView
import cn.yooking.genshin.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Created by yooking on 2023/2/2.
 * Copyright (c) 2023 yooking. All rights reserved.
 */
class GlideUtil {
    companion object {
        fun load(context: BaseActivity, view: ImageView, url: String) {
            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(view)
        }
    }
}