package cn.yooking.genshin.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R
import cn.yooking.genshin.utils.DateUtil
import cn.yooking.genshin.utils.FileUtil
import cn.yooking.genshin.utils.dialog.*
import cn.yooking.genshin.utils.sp.HeaderSpUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.permissionx.guolindev.PermissionX
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

/**
 * Created by yooking on 2022/4/25.
 * Copyright (c) 2022 yooking. All rights reserved.
 */
class HeaderManagerActivity : BaseActivity() {

    //数据缓存
    private var headerName: String = ""
    private var headerNickname: String = ""
    private var checkType: Int = 0

    private val adapter: BaseQuickAdapter<HeaderSpUtil.HeaderEntity, BaseViewHolder> =
        object :
            BaseQuickAdapter<HeaderSpUtil.HeaderEntity, BaseViewHolder>(R.layout.item_header_manager) {
            override fun convert(holder: BaseViewHolder, item: HeaderSpUtil.HeaderEntity) {
                if (item.type == -1) {
                    holder.setImageResource(R.id.iv_header_manager_item, R.mipmap.icon_header_add)
                    holder.setText(R.id.tv_header_manager_item, "添加")
                    return
                }

                val view = holder.getView<ImageView>(R.id.iv_header_manager_item)
                view.setImageURI(Uri.parse(item.path))

                holder.setText(R.id.tv_header_manager_item, item.name)
            }
        }

    override fun initLayoutId(): Int {
        return R.layout.activity_header_manager
    }

    override fun initData() {

    }

    override fun initView() {
        val rvContent = holder.findView<RecyclerView>(R.id.rv_header_manager_content)
        rvContent.layoutManager = GridLayoutManager(this, 3)
        adapter.recyclerView = rvContent
        rvContent.adapter = adapter

        adapter.animationEnable = false

        refresh()
    }

    override fun initListener() {
        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            if (item.type == -1) {//添加
                buildDialog("添加头像", type = -1)
                return@setOnItemClickListener
            }

            buildDialog("修改头像", name = item.name, nickname = item.nickname, type = item.type)
        }
    }

    override fun initClient() {

    }

    private fun refresh() {
        val headerData = HeaderSpUtil.instance.findAllHeader()
        adapter.setNewInstance(headerData)
        adapter.addData(HeaderSpUtil.HeaderEntity("add", "", type = -1))
    }

    private fun buildDialog(
        title: String,
        name: String = "",
        nickname: String = "",
        type: Int = 0
    ) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_upload_header, null)
        val etName = view.findViewById<EditText>(R.id.et_dialog_header_name)
        val etNickname = view.findViewById<EditText>(R.id.et_dialog_header_nickname)
        if (type == 0 || type == 1) {
            etName.isEnabled = false
            etName.setTextColor(resources.getColor(R.color.color_666, theme))
            etName.setText(name)
            etNickname.setText(nickname)
        }

        val rbRole = view.findViewById<RadioButton>(R.id.rb_dialog_header_role)
        val rbArms = view.findViewById<RadioButton>(R.id.rb_dialog_header_arms)

        if (type == 1) {
            rbArms.isChecked = true
        } else {
            rbRole.isChecked = true
        }

        val dialog = createDialog(this, title, view)
            .addListener(TAG_RIGHT, "确定") { _, _ ->
                headerName = etName.text.toString()
                headerNickname = etNickname.text.toString()
                if (headerName.isEmpty()) {
                    Toast.makeText(this, "请输入角色名称", Toast.LENGTH_SHORT).show()
                    return@addListener
                }

                checkType = if (rbRole.isChecked) 0 else 1

                PermissionX.init(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .request { isAllGranted, _, _ ->
                        if (isAllGranted) {
                            val intent = Intent(Intent.ACTION_PICK, null)
                            intent.type = "image/*"
                            startActivityForResult(intent, 10010)
                        }
                    }
            }
            .addListener(TAG_CENTER, "取消")

        if (type != -1) {
            dialog.addListener(TAG_LEFT, "删除") { _, _ ->
                HeaderSpUtil.instance.removeHeader(name)

                refresh()
            }
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10010) {
            if (data != null) {
                val uri = data.data ?: return

                val fileName = "header_img_${DateUtil.date2Str(Date(), "yyyyMMddHHmmss")}.jpg"
                val file = File(FileUtil.getFileDirsPath(this) + fileName)

                UCrop.of(uri, Uri.fromFile(file))
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(512, 512)
                    .start(this)
            }
            return
        }

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            val uri = UCrop.getOutput(data)
            HeaderSpUtil.instance.addHeader(
                headerName,
                HeaderSpUtil.HeaderEntity(headerName, uri?.path ?: "", headerNickname, checkType)
            )

            refresh()
        }
    }
}