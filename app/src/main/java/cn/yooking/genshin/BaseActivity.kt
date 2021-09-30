package cn.yooking.genshin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    lateinit var holder: ViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initLayoutId())

        holder = ViewHolder(this)

        initData()
        initView()
        initListener()
        initClient()
    }

    abstract fun initLayoutId(): Int
    abstract fun initData();
    abstract fun initView();
    abstract fun initListener();
    abstract fun initClient();
}