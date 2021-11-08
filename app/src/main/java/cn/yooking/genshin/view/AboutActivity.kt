package cn.yooking.genshin.view

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.TextView
import android.widget.Toast
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R

/**
 * Created by yooking on 2021/11/8.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class AboutActivity : BaseActivity() {
    override fun initLayoutId(): Int {
        return R.layout.activity_about
    }

    override fun initData() {

    }

    override fun initView() {

    }

    override fun initListener() {
        holder.setOnClickListener(R.id.tv_about_github, R.id.tv_about_download) {
            if (it is TextView) {
                val text = it.text
                val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val newPlainText = ClipData.newPlainText("label", text)
                manager.setPrimaryClip(newPlainText)
                Toast.makeText(this, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun initClient() {

    }
}