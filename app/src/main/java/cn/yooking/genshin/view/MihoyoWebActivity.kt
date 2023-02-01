package cn.yooking.genshin.view

import android.webkit.CookieManager
import android.webkit.WebView
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R
import cn.yooking.genshin.view.model.MihoyoModel

/**
 * Created by yooking on 2023/2/1.
 * Copyright (c) 2023 yooking. All rights reserved.
 */
class MihoyoWebActivity : BaseActivity() {
    lateinit var model:MihoyoModel

    override fun initLayoutId(): Int {
        return R.layout.activity_mihoyo_web
    }

    override fun initData() {
        model = MihoyoModel()
    }

    override fun initView() {
        val web:WebView = holder.findView(R.id.wv_mihoyo_content)
        setWebSettings(web)
        web.loadUrl("https://user.mihoyo.com")
    }

    private fun setWebSettings(web: WebView){
        val settings = web.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
    }

    override fun initListener() {
        holder.setOnClickListener(R.id.btn_mihoyo_read){
            val manager = CookieManager.getInstance()
            val cookie = manager.getCookie("https://user.mihoyo.com")

            model.readAuthKey(this,cookie)
        }
    }

    override fun initClient() {

    }
}