package cn.yooking.genshin.view.model

import android.util.Log
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.utils.dialog.createDialog
import cn.yooking.genshin.utils.okhttp.OkhttpUtil
import cn.yooking.genshin.utils.okhttp.StringCallback
import cn.yooking.genshin.utils.sp.HeaderSpUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by yooking on 2023/2/2.
 * Copyright (c) 2023 yooking. All rights reserved.
 */
class HeaderManagerModel(context: BaseActivity) {

    private val dialog = createDialog(context, "", "正在导入数据")

    fun update(runnable: Runnable) {
        OkhttpUtil.instance.get("https://api-static.mihoyo.com/common/blackboard/ys_obc/v1/home/content/list?app_sn=ys_obc&channel_id=189",
            object : StringCallback() {
                override fun onStart() {
                    dialog.show()
                }

                override fun onResponse(code: Int, response: String) {
                    val jsonObject = JSONObject(response)
                    val data = jsonObject.getJSONObject("data")
                    findHeader(data)
                }

                override fun onError(code: Int, message: String) {

                }

                override fun onEnd() {
                    dialog.dismiss()
                    runnable.run()
                }

            })
    }

    private fun findHeader(data: JSONObject) {
        val list = data.getJSONArray("list")
        val playingMethod = findChildrenById(list, "189") ?: return//找到图鉴
        val playingMethodChildren = playingMethod.getJSONArray("children")

        val roleObj = findChildrenById(playingMethodChildren, "25") ?: return//角色
        val roleArray = roleObj.getJSONArray("list")
        Log.i(
            "HeaderManagerModel",
            "roleArray.length()->${roleArray.length()}"
        )
        for (i in 0 until roleArray.length()) {
            val role = roleArray.getJSONObject(i)
            Log.i("HeaderManagerModel", role.toString())
            val name = role.getString("title")
            val url = role.getString("icon")
            HeaderSpUtil.instance.changeHeaderUrl(name, 0, url)
        }

        val armsObj = findChildrenById(playingMethodChildren, "5") ?: return//武器
        val armsArray = armsObj.getJSONArray("list")
        Log.i(
            "HeaderManagerModel",
            "armsArray.length()->${armsArray.length()}"
        )
        for (i in 0 until roleArray.length()) {
            val arms = armsArray.getJSONObject(i)
            Log.i("HeaderManagerModel", arms.toString())
            val name = arms.getString("title")
            val url = arms.getString("icon")
            HeaderSpUtil.instance.changeHeaderUrl(name, 1, url)
        }
    }

    private fun findChildrenById(childrenArr: JSONArray, targetId: String): JSONObject? {
        for (i in 0 until childrenArr.length()) {
            val obj = childrenArr.getJSONObject(i)
            val id = obj.getString("id")
            if (targetId == id) { // 找到 目标ID
                return obj
            }
        }
        return null
    }
}