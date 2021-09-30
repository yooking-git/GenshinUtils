package cn.yooking.genshin.view.presenter

import android.app.AlertDialog
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import cn.yooking.genshin.R
import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.datasource.data.Record
import cn.yooking.genshin.utils.dialog.createDialog
import cn.yooking.genshin.utils.okhttp.OkhttpUtil
import cn.yooking.genshin.utils.okhttp.StringCallback
import cn.yooking.genshin.view.LotteryAnalysisActivity
import cn.yooking.genshin.view.MainActivity
import com.alibaba.fastjson.JSON
import org.json.JSONObject

/**
 * Created by yooking on 2021/9/24.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class MainPresenter(val context: MainActivity) {
    private var dialog: AlertDialog? = null
    private val data1: MutableList<Record> = arrayListOf()
    private val data2: MutableList<Record> = arrayListOf()
    private val data3: MutableList<Record> = arrayListOf()
    private val data4: MutableList<Record> = arrayListOf()
    private val pageSize = 20
    private var isFirstClient = true
    private var isClientEnd = false

    fun hasAuthKey(url: String): Boolean {
        return url.contains("index.html?") && url.contains("#/log")
    }

    fun clear() {
        isFirstClient = true
        isClientEnd = false
        data1.clear()
        data2.clear()
        data3.clear()
        data4.clear()
    }

    /**
     * @param type 对应gacha_type 301-up抽卡 302-武器up 100-新手抽卡 200-常驻抽卡
     * @param page 页码
     * @param endId 上次查询最后一项的id - 无则传0
     */
    fun clientUrl(type: String = "100", page: Int = 1, endId: String = "0") {
        val url = getUrl(type, page, endId)
        if (url.isEmpty()) return

        if (dialog == null) {
            dialog = createDialog(context, "正在导入数据")
        }

        OkhttpUtil.instance.get(url, object : StringCallback() {
            override fun onStart() {
                context.runOnUiThread {
                    if (dialog != null) {
                        if (isFirstClient) {
                            dialog!!.show()
                        }
                        when (type) {
                            "100" -> {
                                dialog!!.setMessage("正在读取新手卡池数据(${page})")
                            }
                            "200" -> {
                                dialog!!.setMessage("正在读取常驻卡池数据(${page})")
                            }
                            "301" -> {
                                dialog!!.setMessage("正在读取up卡池数据(${page})")
                            }
                            "302" -> {
                                dialog!!.setMessage("正在读取武器卡池数据(${page})")
                            }
                        }

                    }
                    isFirstClient = false
                }
            }

            override fun onResponse(code: Int, response: String) {
                val json = JSONObject(response)
                if (json.has("data")) {
                    val dataJson = json.getJSONObject("data")
                    if (dataJson.has("list")) {
                        val listJson = dataJson.getJSONArray("list");

                        val parseArray: List<Record> =
                            JSON.parseArray(listJson.toString(), Record::class.java)
                        when (type) {
                            "100" -> {//新手
                                data1.addAll(parseArray)
                            }
                            "200" -> {//常驻
                                data2.addAll(parseArray)
                            }
                            "301" -> {//
                                data3.addAll(parseArray)
                            }
                            "302" -> {
                                data4.addAll(parseArray)
                            }
                        }

                        if (parseArray.size == pageSize) {
                            clientUrl(type, page + 1, parseArray[parseArray.size - 1].getId())
                        } else {
                            when (type) {
                                "100" -> {
                                    clientUrl("200")
                                }
                                "200" -> {
                                    clientUrl("301")
                                }
                                "301" -> {
                                    clientUrl("302")
                                }
//                                "302" -> {
//
//                                }
                                else -> {
                                    isClientEnd = true
                                }
                            }
                        }
                    }
                }
            }

            override fun onError(code: Int, message: String) {
                isClientEnd = true
            }

            override fun onEnd() {
                if (isClientEnd) {
                    var saveCount = 0
                    var repeatCount = 0
                    val result1 = SQLiteHelper.instance.save(data1)
                    saveCount += result1[0]
                    repeatCount += result1[1]

                    val result2 = SQLiteHelper.instance.save(data2)
                    saveCount += result2[0]
                    repeatCount += result2[1]

                    val result3 = SQLiteHelper.instance.save(data3)
                    saveCount += result3[0]
                    repeatCount += result3[1]

                    val result4 = SQLiteHelper.instance.save(data4)
                    saveCount += result4[0]
                    repeatCount += result4[1]

                    context.runOnUiThread {

                        if (dialog != null)
                            dialog!!.dismiss()

                        Toast.makeText(
                            context,
                            "本次成功导入了\n新数据：${saveCount}条\n重复数据：${repeatCount}条",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val uid = when {
                        data1.size > 0 -> data1[0].uid
                        data2.size > 0 -> data2[0].uid
                        data3.size > 0 -> data3[0].uid
                        data4.size > 0 -> data4[0].uid
                        else -> ""
                    }
                    if (context.uid.isEmpty()) {
                        context.uid = uid
                        context.holder.setText(R.id.tv_main_user, "(uid:$uid)")
                    }
                    if (uid.isNotEmpty()) {
                        val intent = Intent(context, LotteryAnalysisActivity::class.java)
                        intent.putExtra("uid", uid)
                        context.startActivity(intent)
                    }
                }
            }
        })
    }

    private fun getUrl(type: String, page: Int, endId: String = "0"): String {
        val recordUrl = context.holder.getText(R.id.et_dialog_url)

        if (recordUrl.isEmpty()) return ""

        val size = pageSize

        val regexStart = "index.html?"
        val regexEnd = "#/log"
        val startIndex = recordUrl.indexOf(regexStart) + regexStart.length
        val endIndex = recordUrl.indexOf(regexEnd)

        val authkey = recordUrl.substring(startIndex, endIndex)
//            "authkey_ver=1&sign_type=2&auth_appid=webview_gacha&init_type=301&gacha_id=81b5e01fe6c50a6b9c88e94c51c312b1790141&timestamp=1630453166&lang=zh-cn&device_type=mobile&ext=%7b%22loc%22%3a%7b%22x%22%3a1930.7144775390625%2c%22y%22%3a197.19647216796876%2c%22z%22%3a-1265.9613037109375%7d%2c%22platform%22%3a%22Android%22%7d&game_version=CNRELAndroid2.1.0_R4379056_S4398912_D4398912&region=cn_gf01&authkey=NA2i0sJeV1hjmt9iK28xN6Hx4Ah0LU%2fCvYLSXe3P1NsTHEhvQBg8pgyIavkJ2As%2f2oKgHukRGjZE7uUAbSEz0mN%2b7IPbwOxtHK4wWUdIPcIZf6k9x7pTVZJ%2fh91VYSVtdhHujLQ%2frn233olhakyJA5PGhryTqdkSEkrRx2CeBprS7i93bIUja%2bfRUhCULgerJ%2fu%2fzOuCjecUkvv14pv4ED4a6VvK5GmFmdFkwcBM1YlJ1GViPa5X88TAVDm7h3hQ3POAx9kRxtE8ItZa46AIsJYA%2b2drnxy%2bDRYaySjejKlyw0k097lzQHdxWLGSbwBbOiKn9du2RSNNkhzCoTiS%2fDF%2fNUu6%2f6rIljwIF0Ue%2f1MgZq5xRVB%2bflyDtTY6agtMzx4CjgRZzo3azXG8gPy9%2b3QLSrgBbR2IqHRLANwe76o54bPILgSUqzWCfR%2bCqQCHGU1tKQhlESsDKHZWK92GYxcE0nYhLSpLZO72G%2fvXRh57bT3%2b2R3Rw9Y%2bh4KXaymGtdcDftu1D349fsmlO4n3BZIuuZsEKiAt9Er8UdlWKKVDbT61xtxMo%2bkaskxmvIW5FVU%2faFjcKTh8asqeOtpZSlOznPlEYC1Wi%2bpIClC4HljcznSaEFFz%2f54kvLuEXrTufY7JomnH28g%2fQimrtZbn%2fknJazeE4KLmExE%2ffkkgSodES5ZIzwbF74nh44iMMKVqpbhXAnfyT11h2UzoWp1vhB8ldhVAnroINi4WufxdGza5VwcROazpUKOB3nziV8xtGSeALiOswcYfeTNDkv665rp0t7fxqxt662qpYuzaYUqxU4Z2GsmGr8D1McJQZnGI9FHLS6cB8O9CtGeBrux1vuafjybQLumjGEoKS2qVEZ5PJkAY0o8CzT%2fP95CeMq6lrF7knB%2fqN4U1qWBPL9J9gSEGJzycucZMEfM2KFt8izCtTu%2bOfJXrMT4Gq1mZMGhVuHaSyRe3MTFHfL%2f4kRjDxMyKZjHFnCY3daa8iKswi8sriREDRdvidc59B%2btN1ikkSvZgtEuM%2f49%2f8kcMwTbPPU72INmX0hmi3J5anXjsBZSxud%2fPQ7kG4CZ7mbxdK6QMwMeQuE7iL7vziVTIhhdc8MVCMSLPIceeqUXgCi1xoTX2mEEbLdB7urBiXyH7gdjG0sR1RgQlpuncZK%2bkoGySfFhDIr24fWA3DP65%2ftMnJWB0ZbJUs2oBl1l09%2f2ar9YM4Xyrh01dEUE3Wm53Y33vO9AMoSdP8uIydsdjf%2fRdMGCOOjRDvWL%2bOR8T9kJpUC6wWEekas7DYfqf%2f6IgKfn8N5fPQqi4zVR86kMBW8ED3gZqCV7ZArr%2bhQrcFkIYkOUxQvVHf8%2b8FlwaPPZiKjc1ag%3d%3d&game_biz=hk4e_cn"

        val realUrl = "https://hk4e-api.mihoyo.com/event/gacha_info/api/getGachaLog"

        return "${realUrl}?${authkey}&gacha_type=${type}&page=${page}&size=${size}&end_id=${endId}";
    }
}