package cn.yooking.genshin.widget.echarts.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import cn.yooking.genshin.R


/**
 * 图表插件
 * Created by yooking on 2021/9/23.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class EChartsView : WebView {
    private val eChartsUrl = "file:///android_asset/echarts.html"

    constructor(context: Context)
            : this(context, null);

    constructor(context: Context, attributes: AttributeSet?)
            : this(context, attributes, 0);

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int)
            : super(context, attributes, defStyleAttr) {
        init()
    }

    private fun init() {
        val webSettings = settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportZoom(false)
        webSettings.displayZoomControls = false
        webSettings.setAppCacheEnabled(false)
    }

    /**刷新图表
     * java调用js的loadEcharts方法刷新echart
     * 不能在第一时间就用此方法来显示图表，因为第一时间html的标签还未加载完成，不能获取到标签值
     * @param option
     */
    private fun refreshEchartsWithOption(option: String) {
        this.postDelayed({
            val call = "javascript:loadEcharts('$option')"
            loadUrl(call)
        }, 0)
    }

    fun addDefWebClient(option: String) {
        this.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                refreshEchartsWithOption(option)
            }
        }

        //防止因为加载过快导致白屏...
        this.postDelayed({
            loadUrl(eChartsUrl)
        }, 0)
    }
}