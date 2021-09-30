package cn.yooking.genshin.view

import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R
import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.datasource.data.User
import cn.yooking.genshin.utils.dialog.TAG_CENTER
import cn.yooking.genshin.utils.dialog.TAG_RIGHT
import cn.yooking.genshin.utils.dialog.addListener
import cn.yooking.genshin.utils.dialog.createChoiceDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Created by yooking on 2021/9/27.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class RecordActivity : BaseActivity() {

    val data = mutableListOf<Map<String, String>>()
    var uid: String = ""
    var user: User? = null

    val adapter =
        object :
            BaseQuickAdapter<Map<String, String>, BaseViewHolder>(R.layout.item_record_list) {
            override fun convert(holder: BaseViewHolder, item: Map<String, String>) {
                val color = when (item["rank_type"]) {
                    "5" -> R.color.color_stars5
                    "4" -> R.color.color_stars4
                    else -> R.color.color_stars3
                }
                holder.setText(R.id.tv_record_item_name, item["name"])
                    .setTextColor(
                        R.id.tv_record_item_name,
                        ContextCompat.getColor(context, color)
                    )

                holder.setText(
                    R.id.tv_record_item_type,
                    "${item["rank_type"]}星${item["item_type"]}"
                )
                    .setTextColor(
                        R.id.tv_record_item_type,
                        ContextCompat.getColor(context, color)
                    )

                holder.setText(R.id.tv_record_item_num, item["count"])
                    .setTextColor(
                        R.id.tv_record_item_num,
                        ContextCompat.getColor(context, color)
                    )
            }
        }

    override fun initLayoutId(): Int {
        return R.layout.activity_record
    }

    override fun initData() {
        uid = intent.getStringExtra("uid")!!
        user = SQLiteHelper.instance.findUser(uid)
        data.addAll(SQLiteHelper.instance.findAllRecordCount(uid))
    }

    override fun initView() {
        if (user != null) {
            val nickname = "${user!!.nickname}(uid:${user!!.uid})"
            holder.setText(R.id.tv_record_user, nickname)
        }
        val rvContent = holder.findView<RecyclerView>(R.id.rv_record_content)
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter.recyclerView = rvContent
        rvContent.adapter = adapter

        adapter.setNewInstance(data)

        adapter.setEmptyView(R.layout.include_empty)
    }


    override fun initListener() {
        val arr = arrayOf("全部卡池", "角色卡池", "常驻卡池", "武器卡池", "新手卡池")
        var which = 0
        holder.setOnClickListener(R.id.tv_record_type) {
            val dialog = createChoiceDialog(
                this,
                "请选择要展示的卡池",
                which, arr
            ) { _, i ->
                which = i
            }
            dialog.addListener(TAG_RIGHT, "确定") { _, _ ->
                val newData = mutableListOf<Map<String, String>>()
                when (which) {
                    0 -> {
                        newData.addAll(SQLiteHelper.instance.findAllRecordCount(uid))
                    }
                    1 -> {
                        newData.addAll(SQLiteHelper.instance.findAllRecordCount(uid, "301"))
                    }
                    2 -> {
                        newData.addAll(SQLiteHelper.instance.findAllRecordCount(uid, "200"))
                    }
                    3 -> {
                        newData.addAll(SQLiteHelper.instance.findAllRecordCount(uid, "302"))
                    }
                    4 -> {
                        newData.addAll(SQLiteHelper.instance.findAllRecordCount(uid, "100"))
                    }
                }
                holder.setText(R.id.tv_record_type, "卡池切换:${arr[which].replace("卡池", "")}")
                adapter.setNewInstance(newData)
            }.addListener(TAG_CENTER, "取消")
            dialog.show()
        }
    }

    override fun initClient() {

    }
}