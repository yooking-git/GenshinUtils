package cn.yooking.genshin.view

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R
import cn.yooking.genshin.adapter.DraggableAdapter
import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.utils.dialog.TAG_CENTER
import cn.yooking.genshin.utils.dialog.TAG_RIGHT
import cn.yooking.genshin.utils.dialog.addListener
import cn.yooking.genshin.utils.dialog.createDialog
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 用户管理界面
 * Created by yooking on 2021/9/24.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class UserListActivity : BaseActivity() {

    private val adapter: DraggableAdapter<MutableMap<String, String>, BaseViewHolder> =
        object :
            DraggableAdapter<MutableMap<String, String>, BaseViewHolder>(R.layout.item_user_list) {
            override fun convert(holder: BaseViewHolder, item: MutableMap<String, String>) {
                val nickname = item["nickname"]
                val uid = item["uid"]
                val allCount = item["all"]?.toInt() ?: 0
                val stars5 = item["stars5"]?.toInt() ?: 0
                val stars4 = item["stars4"]?.toInt() ?: 0

                holder.setText(R.id.tv_user_nickname, nickname)
                    .setText(R.id.tv_user_uid, uid)

                holder.setText(
                    R.id.tv_user_statistics,
                    "抽卡${allCount}次，" +
                            "五星${stars5}次，" +
                            "四星${stars4}次"
                )
            }
        }

    val data = mutableListOf<MutableMap<String, String>>()

    override fun initLayoutId(): Int {
        return R.layout.activity_user_list
    }

    override fun initData() {
        val allUser = SQLiteHelper.instance.findAllUser()
        for (user in allUser) {
            val records = user.findRecord()
            val allCount = records.size
            var stars4 = 0
            var stars5 = 0
            for (record in records) {
                when (record.rank_type) {
                    "4" -> stars4++
                    "5" -> stars5++
                }
            }
            val userMap = mutableMapOf(
                "nickname" to user.nickname,
                "uid" to user.uid,
                "all" to "$allCount",
                "stars5" to "$stars5",
                "stars4" to "$stars4"
            )
            data.add(userMap)
        }
    }

    override fun initView() {
        val rvContent: RecyclerView = holder.findView(R.id.rv_user_content)
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter.recyclerView = rvContent
        rvContent.adapter = adapter

        adapter.setNewInstance(data)
        adapter.setEmptyView(R.layout.include_empty)

        //设置拖动按钮
        adapter.draggableModule.toggleViewId = R.id.iv_user_drag
        //设置可拖动
        adapter.draggableModule.isDragEnabled = true
        //设置需要长按才可拖动
        adapter.draggableModule.isDragOnLongPressEnabled = true
        //设置可滑动删除
        adapter.draggableModule.isSwipeEnabled = true
        //设置拖动事件监听
        adapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder?,
                from: Int,
                target: RecyclerView.ViewHolder?,
                to: Int
            ) {

            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                val data = adapter.data
                val uidArr = Array(data.size) { data[it]["uid"] ?: "" }
                SQLiteHelper.instance.changeUserSort(uidArr = uidArr)
            }
        })
    }

    override fun initListener() {
        adapter.addChildClickViewIds(
            R.id.tv_user_analysis,
            R.id.iv_user_edit,
            R.id.tv_user_myrecord,
            R.id.tv_item_user_delete
        )
        adapter.setOnItemChildClickListener { _, view, position ->
            val item = adapter.getItem(position)
            val uid = item["uid"]
            when (view.id) {
                R.id.tv_user_analysis -> {
                    val intent = Intent(
                        this@UserListActivity,
                        LotteryAnalysisActivity::class.java
                    )
                    intent.putExtra("uid", uid)
                    startActivity(intent)
                }
                R.id.iv_user_edit -> {
                    val inputView = createInputView()
                    val dialog = createDialog(this@UserListActivity, "昵称修改(UID:${uid})", inputView)
                    dialog.addListener(
                        TAG_RIGHT, "确定"
                    ) { _, _ ->
                        val nickname = holder.getText(R.id.et_dialog_nickname)
                        SQLiteHelper.instance.updateNickname(uid ?: "unknown", nickname)
                        item["nickname"] = nickname
                        adapter.notifyItemChanged(position)
                    }.addListener(TAG_CENTER, "取消")
                    dialog.show()
                }
                R.id.tv_user_myrecord -> {
                    val intent = Intent(
                        this@UserListActivity,
                        RecordActivity::class.java
                    )
                    intent.putExtra("uid", uid)
                    startActivity(intent)
                }
                R.id.tv_item_user_delete -> {
                    createDialog(
                        this,
                        "删除提醒",
                        "本次删除仅删除(uid：${uid})的用户相关信息，不删除抽卡记录，下次导入时将重新合并数据。\n确认是否删除数据？"
                    ).addListener(
                        TAG_RIGHT, "确定"
                    ) { _, _ ->
                        SQLiteHelper.instance.deleteUserWithoutRecord(uid!!)
                        adapter.remove(adapter.getItem(position))
                    }.addListener(TAG_CENTER, "取消").show()

                }
            }
        }
    }

    private fun createInputView(): View {
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_input_nickname, null)
        val etNickname = view.findViewById<EditText>(R.id.et_dialog_nickname)
        holder.saveView(etNickname)

        return view
    }

    override fun initClient() {

    }
}