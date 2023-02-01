package cn.yooking.genshin.view.model

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.utils.dialog.createDialog
import cn.yooking.genshin.utils.okhttp.OkhttpUtil
import com.qyinter.yuanshenlink.util.Md5Util
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URLEncoder
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.floor

/**
 * Created by yooking on 2023/2/1.
 * Copyright (c) 2023 yooking. All rights reserved.
 */
class MihoyoModel {

    private var dialog: AlertDialog? = null

    private val TAG: String = "MihoyoModel";

    private var account_id = ""
    private var weblogin_token = ""
    private var nCookie = ""
    private var url = ""

    private var roleList = mutableListOf<MutableMap<String, String>>()

    fun readAuthKey(context: BaseActivity,cookie: String) {
        thread {
            context.runOnUiThread{
                if(dialog == null){
                    dialog = createDialog(context,"","正在读取数据...")
                }
                dialog!!.show()
            }
            Log.i(TAG, "cookie=>${cookie}")

            loginByCookie(cookie)
            getMultiTokenByLoginTicket(cookie)
            getUserGameRolesByCookie()
            getAuthKey()

            val manager = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val newPlainText = ClipData.newPlainText("Label", url)
            manager.setPrimaryClip(newPlainText)
            context.runOnUiThread{
                Toast.makeText(context, "url已复制到剪贴板", Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()
            }
        }
    }

    private fun showDialog(context: BaseActivity){

    }

    /**
     * 米游社登录
     */
    private fun loginByCookie(cookie: String) {

        val time = Date().time
        val mihoyoIdBodyStr =
            client("https://webapi.account.mihoyo.com/Api/login_by_cookie?t=${time}", cookie)

        //{
        //  "code": 200,
        //  "data": {
        //    "account_info": {
        //      "account_id": 20698247,
        //      "area_code": "+86",
        //      "create_time": 1509767414,
        //      "identity_code": "350************338",
        //      "is_adult": 1,
        //      "mobile": "181****5976",
        //      "real_name": "**坤",
        //      "safe_level": 2,
        //      "weblogin_token": "cJR8m9v6XT6MV6uSkpzUM5AlqxTJql9cZDNiCQS3"
        //    },
        //    "msg": "成功",
        //    "notice_info": {},
        //    "status": 1
        //  }
        //}
        Log.i(TAG, "mihoyoId=>${mihoyoIdBodyStr}")
        if (mihoyoIdBodyStr == null) return

        val json = JSONObject(mihoyoIdBodyStr)
        val data = json.getJSONObject("data")
        val accountInfo = data.getJSONObject("account_info")
        account_id = accountInfo.getString("account_id")
        weblogin_token = accountInfo.getString("weblogin_token")
    }

    /**
     * 读取各游戏token
     */
    private fun getMultiTokenByLoginTicket(cookie: String) {
        val tidBodyStr = client(
            "https://api-takumi.mihoyo.com/auth/api/getMultiTokenByLoginTicket?login_ticket=${weblogin_token}&token_types=3&uid=${account_id}",
            cookie
        )

        //{
        //  "retcode": 0,
        //  "message": "OK",
        //  "data": {
        //    "list": [
        //      {
        //        "name": "stoken",
        //        "token": "lBWPQCUmVxkNK3nOCNCT6zyIKS4uznzLOz1zChOA"
        //      },
        //      {
        //        "name": "ltoken",
        //        "token": "9yJnSrMJxMEwy20MHA2si9RNrdtF3se19MN7cwSu"
        //      }
        //    ]
        //  }
        //}
        Log.i(TAG, "tid=>${tidBodyStr}")
        if (tidBodyStr == null) return

        val json = JSONObject(tidBodyStr)
        val data = json.getJSONObject("data")
        val list = data.getJSONArray("list")

        nCookie = "stuid=${account_id};"
        for (i in 0 until list.length()) {
            val obj = list.getJSONObject(i)
            val name = obj.getString("name")
            val token = obj.getString("token")
            nCookie += "${name}=${token};"
        }
        nCookie += cookie
        Log.i(TAG, "nCookie=>${nCookie}")
    }

    private fun client(url: String, cookie: String): String? {
        val req = Request.Builder()
            .url(url)
            .header("Cookie", cookie)
            .build()
        val call = OkhttpUtil.instance.client().newCall(req)
        val response = call.execute()
        return response.body?.string()
    }

    /**
     * 读取游戏角色
     */
    private fun getUserGameRolesByCookie() {
        val uidReq = Request.Builder()
            .url("https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=hk4e_cn")
            .header("Cookie", nCookie)
            .build()
        val uidCall = OkhttpUtil.instance.client().newCall(uidReq)
        val uidResponse = uidCall.execute()
        val uidBodyStr = uidResponse.body?.string()

        //{
        //  "retcode": 0,
        //  "message": "OK",
        //  "data": {
        //    "list": [
        //      {
        //        "game_biz": "hk4e_cn",
        //        "region": "cn_gf01",
        //        "game_uid": "194399013",
        //        "nickname": "不卜庐采药童",
        //        "level": 58,
        //        "is_chosen": true,
        //        "region_name": "天空岛",
        //        "is_official": true
        //      }
        //    ]
        //  }
        //}
        Log.i(TAG, "uid=>${uidBodyStr}")
        if (uidBodyStr == null) return

        val json = JSONObject(uidBodyStr)
        val data = json.getJSONObject("data")
        val list = data.getJSONArray("list")
        for (i in 0 until list.length()) {
            val obj = list.getJSONObject(i)
            val map = mutableMapOf<String, String>()
            for (key in obj.keys()) {
                map[key] = obj.getString(key)
            }
            roleList.add(map)
        }
    }

    /**
     * 获取authKey并将url复制到剪贴板
     */
    private fun getAuthKey() {
        if (roleList.size == 0) return

        val it = roleList[0]
        val gameUid = it["game_uid"] ?: ""
        val gameBiz = it["game_biz"] ?: ""
        val region = it["region"] ?: ""
        val postJson = authKeyPostJson(gameUid, gameBiz, region)
        val requestBody = postJson.toRequestBody("application/json;charset=utf-8".toMediaType())
        val authKeyReq = Request.Builder()
            .url("https://api-takumi.mihoyo.com/binding/api/genAuthKey")
            .header("Content-Type", "application/json;charset=utf-8")
            .header("Host", "api-takumi.mihoyo.com")
            .header("Accept", "application/json, text/plain, */*")
            .header("x-rpc-app_version", "2.28.1")
            .header("x-rpc-client_type", "5")
            .header("x-rpc-device_id", "CBEC8312-AA77-489E-AE8A-8D498DE24E90")
            .header("DS", getDs())
            .header("Cookie", nCookie)
            .post(requestBody)
            .build()
        val authKeyCall = OkhttpUtil.instance.client().newCall(authKeyReq)
        val authKeyResponse = authKeyCall.execute()
        val authKeyBodyStr = authKeyResponse.body?.string()


        Log.i(TAG, "authKey=>${authKeyBodyStr}")
        if (authKeyBodyStr == null) return

        val json = JSONObject(authKeyBodyStr)
        val data = json.getJSONObject("data")

        val signType = data.getString("sign_type")
        val authkeyVer = data.getString("authkey_ver")
        val authkey = URLEncoder.encode(data.getString("authkey"), "utf-8")

        url =
            "https://hk4e-api.mihoyo.com/event/gacha_info/api/getGachaLog?win_mode=fullscreen&authkey_ver=${authkeyVer}&sign_type=${signType}&auth_appid=webview_gacha&init_type=301&gacha_id=b4ac24d133739b7b1d55173f30ccf980e0b73fc1&lang=zh-cn&device_type=mobile&game_version=CNRELiOS3.0.0_R10283122_S10446836_D10316937&plat_type=ios&game_biz=${gameBiz}&size=20&authkey=${authkey}&region=${region}&timestamp=1664481732&gacha_type=200&page=1&end_id=0"


    }

    private fun authKeyPostJson(gameUid: String, gameBiz: String, region: String): String {
        return "{\"game_biz\":\"${gameBiz}\"" +
                ",\"region\":\"${region}\"" +
                ",\"game_uid\":\"${gameUid}\"" +
                ",\"auth_appid\":\"webview_gacha\"" +
                "}"
    }

    private fun getDs(): String {
        val salt = "ulInCDohgEs557j0VsPDYnQaaz6KJcv5"
        val time = Date().time / 1000
        val str = this.getStr()
        val key = "salt=${salt}&t=${time}&r=${str}"
        val md5 = Md5Util.getMD5(key)

        return "${time},${str},${md5}"
    }

    private fun getStr(): String {
        val chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678"
        val maxPos = chars.length
        var code = ""
        for (i in 0..5) {
            code += chars[floor(Math.random() * maxPos).toInt()]
        }
        return code
    }
}