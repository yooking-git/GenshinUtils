package cn.yooking.genshin.view

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R
import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.datasource.data.Record
import cn.yooking.genshin.utils.DateUtil
import cn.yooking.genshin.utils.FileUtil
import cn.yooking.genshin.utils.dialog.*
import com.alibaba.fastjson.JSON
import java.util.*
import kotlin.concurrent.thread

/**
 * 数据导入/导出
 * Created by yooking on 2021/9/24.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class DataStorageActivity : BaseActivity() {
    override fun initLayoutId(): Int {
        return R.layout.activity_data_storage
    }

    override fun initData() {

    }

    override fun initView() {

        val filePath = FileUtil.getFileDirsUrl(this)
        val hintStart = "数据存储路径:"
        val hintEnd = "/storage/emulated/0一般在[文件管理器]中命名为[手机存储]"
        val hintStr = "$hintStart\n$filePath\n$hintEnd"
        val spannableString = SpannableString(hintStr)
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            hintStart.length,
            hintStart.length + filePath.length + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        holder.findView<TextView>(R.id.tv_data_storage_hint)
            .text = spannableString

    }

    override fun initListener() {
        holder.setOnClickListener(
            R.id.tv_data_storage_export,
            R.id.tv_data_storage_import,
            R.id.tv_data_storage_delete
        ) {
            when (it.id) {
                R.id.tv_data_storage_export -> {//导出
                    var which = 0
                    val allUser = SQLiteHelper.instance.findAllUser()
                    val array =
                        Array(allUser.size) { i -> "${allUser[i].nickname}(${allUser[i].uid})" }
                    if (array.isEmpty()) {
                        Toast.makeText(
                            this@DataStorageActivity,
                            "未找到抽卡记录，请先导入数据",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    val dialog = createChoiceDialog(
                        this,
                        "请选择要导出的用户数据",
                        which, array
                    ) { _, i ->
                        which = i
                    }
                    dialog.addListener(TAG_RIGHT, "确定") { _, _ ->
                        export(allUser[which].uid)
                    }.addListener(TAG_CENTER, "取消")
                    dialog.show()
                }
                R.id.tv_data_storage_import -> {//导入
                    showListFileDialog("请选择要导入的数据", object : FileListCallback {
                        override fun selectedFile(fileName: String) {
                            import(fileName)
                        }
                    })
                }
                R.id.tv_data_storage_delete -> {//删除
                    showListFileDialog("请选择要删除的数据", object : FileListCallback {
                        override fun selectedFile(fileName: String) {
                            delete(fileName)
                        }
                    })
                }
            }
        }
    }

    private fun showListFileDialog(title: String, callback: FileListCallback) {
        //获取文件列表
        val nameArray = FileUtil.readFileNameList(this)
        val showNameArray = Array(nameArray.size) { i ->
            var name = nameArray[i]
            if (name.contains("d")) {
                val split = name.split("d")
                if (split.size == 2) {
                    val uid = split[0]
                    val time = DateUtil.formatStr(split[1].replace(".json", ""))
                    val user = SQLiteHelper.instance.findUser(uid)
                    if (user != null && user.nickname.isNotEmpty()) {
                        name = "${user.nickname}($uid)\n$time"
                    }
                }
            }
            name
        }
        if (nameArray.isEmpty()) {
            Toast.makeText(
                this,
                "路径${getExternalFilesDir("json")!!.absolutePath}下无数据",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        var which = 0
        val dialog = createChoiceDialog(this, title, which, showNameArray) { _, i ->
            which = i
        }
        dialog.addListener(TAG_RIGHT, "确定") { _, _ ->
            callback.selectedFile(nameArray[which])
        }.addListener(TAG_CENTER, "取消")
        dialog.show()
    }

    private fun export(uid: String) {
        thread(start = true) {
            val fileName = "${uid}d${DateUtil.date2Str(Date(), "yyyyMMddHHmmss")}.json"
            val recordList = SQLiteHelper.instance.findAllRecord(uid)
            val size = recordList.size
            val json = JSON.toJSONString(recordList)
            FileUtil.saveAsJson(this, fileName, json)
            runOnUiThread {
                Toast.makeText(this, "本次成功导出数据：${size}条", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun import(fileName: String) {
        thread(start = true) {
            val json = FileUtil.readJson(this, fileName)
            if (json.isNotEmpty()) {
                val data = mutableListOf<Record>()
                data.addAll(JSON.parseArray(json, Record::class.java))
                val result = SQLiteHelper.instance.save(data)

                runOnUiThread {
                    Toast.makeText(
                        this,
                        "本次成功导入了\n新数据：${result[0]}条\n重复数据：${result[1]}条",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "数据格式错误，导入失败",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun delete(fileName: String) {
        var name = fileName
        if (name.contains("d")) {
            val split = name.split("d")
            if (split.size == 2) {
                val uid = split[0]
                val time = DateUtil.formatStr(split[1].replace(".json", ""))
                val user = SQLiteHelper.instance.findUser(uid)
                if (user != null && user.nickname.isNotEmpty()) {
                    name = "${user.nickname}($uid) $time"
                }
            }
        }
        createDialog(this, "数据一经删除不可恢复！！！", "请确认是否删除文件\n[${name}]？")
            .addListener(TAG_RIGHT, "确定") { _, _ ->
                val isDeleted = FileUtil.delete(this, fileName)
                Toast.makeText(
                    this,
                    "删除${fileName}${if (isDeleted) "成功" else "失败"}",
                    Toast.LENGTH_SHORT
                ).show()
            }.addListener(TAG_CENTER, "取消")
            .show()

    }

    override fun initClient() {

    }

    private interface FileListCallback {
        fun selectedFile(fileName: String)
    }
}