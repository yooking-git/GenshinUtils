package cn.yooking.genshin.view

import android.content.ClipboardManager
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R
import cn.yooking.genshin.datasource.SQLiteHelper
import cn.yooking.genshin.utils.NoMultipleItemClickListener
import cn.yooking.genshin.utils.UrlUtils
import cn.yooking.genshin.utils.dialog.*
import cn.yooking.genshin.view.presenter.MainPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class MainActivity : BaseActivity() {

    private val presenter = MainPresenter(this)

    private val data: MutableList<Map<String, String>> = arrayListOf()
    private val adapter: BaseQuickAdapter<Map<String, String>, BaseViewHolder> =
        object : BaseQuickAdapter<Map<String, String>, BaseViewHolder>(R.layout.item_main_list) {
            override fun convert(holder: BaseViewHolder, item: Map<String, String>) {
                holder.setText(R.id.tv_main_item_title, item["title"])
            }
        }

    var uid = ""

    override fun initLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        data.add(mapOf("title" to "用户管理", "tag" to "user"))
        data.add(mapOf("title" to "导入新的抽卡记录", "tag" to "net"))
        data.add(mapOf("title" to "抽卡记录分析", "tag" to "analysis"))
        data.add(mapOf("title" to "抽卡记录统计", "tag" to "result"))
        data.add(mapOf("title" to "本地化导入/导出", "tag" to "local"))
    }

    override fun initView() {
        val rvContent = holder.findView<RecyclerView>(R.id.rv_main_content)
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter.recyclerView = rvContent
        rvContent.adapter = adapter

        adapter.setNewInstance(data)

        val allUser = SQLiteHelper.instance.findAllUser()
        if (allUser.size > 0) {
            uid = allUser[0].uid
            holder.setText(R.id.tv_main_user, "${allUser[0].nickname}(uid:${allUser[0].uid})")
            holder.setText(R.id.tv_main_lasttime, "更新时间：${allUser[0].lastDate}")
        }
    }

    override fun initListener() {
        holder.setOnClickListener(R.id.ll_main_user, clickListener = object : View.OnClickListener {
            var which = 0
            override fun onClick(v: View?) {
                //读取本地数据
                val allUser = SQLiteHelper.instance.findAllUser()

                if (allUser.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "未找到抽卡记录，请先导入数据",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (allUser.size == 1) {
                    intent.putExtra("uid", allUser[0].uid)
                    startActivity(intent)
                    return
                }

                val array =
                    Array(allUser.size) { "${allUser[it].nickname}(${allUser[it].uid})" }

                val dialog = createChoiceDialog(
                    this@MainActivity,
                    "请选择当前用户",
                    which, array
                ) { _, i ->
                    which = i
                }
                dialog.addListener(TAG_RIGHT, "确定") { _, _ ->
                    uid = allUser[which].uid
                    holder.setText(
                        R.id.tv_main_user,
                        "${allUser[which].nickname}(uid:${allUser[which].uid})"
                    )
                    holder.setText(R.id.tv_main_lasttime, "更新时间：${allUser[which].lastDate}")
                }.addListener(TAG_CENTER, "取消")
                dialog.show()
            }
        })

        adapter.setOnItemClickListener(object : NoMultipleItemClickListener() {
            override fun onItemClick(a: BaseQuickAdapter<*, *>, view: View, position: Int) {
                if (!clickEnable(view)) return

                val item = adapter.getItem(position)
                if (item.containsKey("tag")) {
                    when (item["tag"]) {
                        "net" -> {
                            val inputView = createInputView()
                            val dialog = createDialog(this@MainActivity, "请输入链接地址", inputView)
                            dialog.addListener(
                                TAG_RIGHT, "确定"
                            ) { _, _ ->
                                presenter.clear()
                                presenter.clientUrl()
                            }.addListener(TAG_CENTER, "取消")
                            dialog.show()
                        }
                        "analysis" -> {
                            if (uid.isNotEmpty()) {
                                val intent = Intent(
                                    this@MainActivity,
                                    LotteryAnalysisActivity::class.java
                                )
                                intent.putExtra("uid", uid)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "暂无用户数据，请先选择用户",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "user" -> {
                            val intent = Intent(
                                this@MainActivity,
                                UserListActivity::class.java
                            )
                            startActivity(intent)
                        }
                        "local" -> {
                            val intent = Intent(
                                this@MainActivity,
                                DataStorageActivity::class.java
                            )
                            startActivity(intent)
                        }
                        "result" -> {
                            if (uid.isNotEmpty()) {
                                val intent = Intent(
                                    this@MainActivity,
                                    RecordActivity::class.java
                                )
                                intent.putExtra("uid", uid)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "暂无用户数据，请先选择用户",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

        })
    }

    private fun createInputView(): View {
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_input_url, null)
        val etUrl = view.findViewById<EditText>(R.id.et_dialog_url)
        holder.saveView(etUrl)
        etUrl.setText("https://user.mihoyo.com/?im_out=false&sign_type=2&auth_appid=im_ccs&authkey_ver=1&win_direction=portrait&lang=zh-cn&device_type=mobile&ext=%7b%22loc%22%3a%7b%22x%22%3a2254.6728515625%2c%22y%22%3a217.0330810546875%2c%22z%22%3a-892.0397338867188%7d%2c%22platform%22%3a%22Android%22%7d&game_version=CNRELAndroid2.2.0_R4705718_S4715326_D4715326&plat_type=android&authkey=q8bArSDThcOuxT63SoBg4OKpI4olTpruzulc4I%2bhOVGopxW%2f75x6Bax9s8IeWyj9L1BmgY5aaYfIHHAmWDDYWnOhA6BgtaQqNo4FM7RC7xXjPXxXLO7uvxBGnPKGtG1s2ZdzX89MWMwu%2bAKmTR7Y9kRhxTppnZk5z7BewGlt%2f611a3itB8kYTIm9fahDnwbi3D2NEKQ3OuZJMp69GddZheYug%2fZmUNq1%2fQpLSXZmx1z8R01tQp7H2W3Qt0mQN%2fEMYRkjX4FbsifGyR46Wd4evbJDvFGMnh%2f0Dd%2f%2b%2b3b%2fdFTN1TM%2fAXZQWcVyTTPHVFRUMef6E4MfK3VfXgy0p8Bz7sJUBVKSvZgxqK8P3ejqmR8BNtBdkt1FZt0RpZ2q%2blU6P3vPvpvYepM5yb7w0zz5t4eNg3O%2b1RvJQAF2RjF2gSirvecUHKDK7gnfA6qYj7DkX0DojUa9w7TY%2bydz1ZxocuQLmr12rNNLdoLiXQucStVP1jAGFlpS6rcNrkZN0mgu%2bau6CugdguVJWLt54HAgpwHuvoDlCXXofCjulR7lB5VGNf%2b0u06riWIlyfoBPWM52Qz0ntYJ10dOP%2bzNBoAf%2buxDVciBrmZhzZlRN8i9IR%2bp%2bkafuHL7ugviBehiLgEEqskckbuEISo%2f3xUWqIYZccKsuRZa8ln6xH%2bZanyn3RmjFOyFFoUHwoE0BlRAEDLDE8X5OzbbyYNwi9bZXcwgnKHs4R2txTxL8%2bGeuwxranmE9SUD1uDdqCfAq%2bpke3jXSP26BJHIPmHgD01xv0c9af9A8eJ6rczGwRcTLPAbBhG73h4C3HIBySgyO91qNcATuPYAbcgz1MyaYuVet%2f6FRmcaN2oTwqffH%2blCQxr8RDRcNam0gm1Pe5kqabJpS2uaRU3mzoRRFjwlpS5phXoBtOGOYNUbuvi7GqmEs1kzpbrWcc6hecAeyeiVbQuvX4aqEOUoK5ZoFPRul46ZeCTQFzMimrGXBzPLOLNrGiVZJyCHfLmDN%2fqBkR2bWJdpkfeks5kEN96xm0kLKujCEhzoLi0Qr9X15YXooSDOXfnrLUZWDgqV1p59JKyw6XKDfgWMJbCSAB8nnnWCCrmAGt0%2bQvFLrb5DD4uwv9YJWLU1hkB7wpXjjkG8rd04Rtu87naGj%2fcitdytY%2blze2OHssa%2bQcLGKEJaC9%2f3KFe6PBTIAH6szaK%2fsOKcNf4fztXA4ZAPNbhYon6SX3acueTpzftCGT2E7dGo%2b1OFdKdw2XCNJnF7c55BaUDBEicJj7ikv0oFdHq1NQOjo9PByRDsX2W1S64EPzNAaoQOq6tgAHRZyl9ja9uOOZ9WbHqLuYfrmhVJ3a3hmEruqgFuwmleVRP67w%3d%3d&game_biz=hk4e_cn#/login/captcha")

        Log.e("url->map", UrlUtils.getUrlParams(etUrl.text.toString()).toString())
        //获取剪贴板内容 1.剪贴板有内容 2.剪贴板内容包含关键字
        val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = manager.primaryClip
        val clipString = clipData?.getItemAt(0)?.text?.toString() ?: ""
        if (clipString.isNotEmpty() && presenter.hasAuthKey(clipString)) {
            etUrl.setText(clipString)
            Toast.makeText(this, "已自动读取剪贴板内容，如有误请修改", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun initClient() {

    }


}