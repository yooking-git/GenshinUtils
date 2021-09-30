package cn.yooking.genshin.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

/**
 * Created by yooking on 2021/9/27.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class ShotUtil {
    companion object {


        /**
         *
         */
        fun shotView(v: View?): Bitmap? {
            if (v == null) {
                return null
            }
            val screenshot: Bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.RGB_565)
            val c = Canvas(screenshot)
            c.translate(-v.scrollX.toFloat(), -v.scrollY.toFloat())
            v.draw(c)
            return screenshot
        }
    }
}