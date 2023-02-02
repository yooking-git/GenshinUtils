package cn.yooking.genshin.view

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R
import cn.yooking.genshin.YApplication
import cn.yooking.genshin.utils.GlideUtil
import cn.yooking.genshin.utils.HeaderUtil
import cn.yooking.genshin.view.model.LotteryAnalysisModel2
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Created by yooking on 2022/4/24.
 * Copyright (c) 2022 yooking. All rights reserved.
 */
class LotteryAnalysis2Activity : BaseActivity() {

    private lateinit var model: LotteryAnalysisModel2

    private val upAdapter: MyAdapter = MyAdapter(this)
    private val armsAdapter: MyAdapter = MyAdapter(this)
    private val permanentAdapter: MyAdapter = MyAdapter(this)

    override fun initLayoutId(): Int {
        return R.layout.activity_lottery_analysis2
    }

    override fun initData() {
        val uid = intent.getStringExtra("uid") ?: ""
        model = LotteryAnalysisModel2(uid)
        HeaderUtil.instance.init()
    }

    override fun initView() {
        // 总计
        holder.setText(R.id.tv_lottery_analysis2_uid, model.uid)
        holder.setText(R.id.tv_lottery_analysis2_total_fortune, model.fortune)
        holder.getSaveView<TextView>(R.id.tv_lottery_analysis2_total_fortune)
            .setTextColor(resources.getColor(model.fortuneColor, theme))
        holder.setText(R.id.tv_lottery_analysis2_total_starts5_per, model.averageTimes)
        holder.setText(R.id.tv_lottery_analysis2_total_times, model.awardTimes)
        holder.setText(R.id.tv_lottery_analysis2_total_starts5, model.starts5Times)

        // 角色抽卡统计
        holder.setText(R.id.tv_lottery_analysis2_up_fortune, model.upData.fortune)
        holder.getSaveView<TextView>(R.id.tv_lottery_analysis2_up_fortune)
            .setTextColor(resources.getColor(model.upData.fortuneColor, theme))
        holder.setText(
            R.id.tv_lottery_analysis2_up_without_times,
            "已${model.upData.withoutTimes}抽未出金"
        )
        holder.setText(R.id.tv_lottery_analysis2_up_starts5_per, model.upData.averageTimes)
        holder.setText(R.id.tv_lottery_analysis2_up_times, model.upData.awardTimes)
        holder.setText(R.id.tv_lottery_analysis2_up_starts5, model.upData.starts5Times)

        addAdapter(
            R.id.rv_lottery_analysis2_up_content,
            model.upData.legendData,
            upAdapter
        )

        // 武器抽卡统计
        holder.setText(R.id.tv_lottery_analysis2_arms_fortune, model.armsData.fortune)
        holder.getSaveView<TextView>(R.id.tv_lottery_analysis2_arms_fortune)
            .setTextColor(resources.getColor(model.armsData.fortuneColor, theme))
        holder.setText(
            R.id.tv_lottery_analysis2_arms_without_times,
            "已${model.armsData.withoutTimes}抽未出金"
        )
        holder.setText(R.id.tv_lottery_analysis2_arms_starts5_per, model.armsData.averageTimes)
        holder.setText(R.id.tv_lottery_analysis2_arms_times, model.armsData.awardTimes)
        holder.setText(R.id.tv_lottery_analysis2_arms_starts5, model.armsData.starts5Times)
        addAdapter(
            R.id.rv_lottery_analysis2_arms_content,
            model.armsData.legendData,
            armsAdapter
        )

        // 常驻抽卡统计
        holder.setText(R.id.tv_lottery_analysis2_permanent_fortune, model.permanentData.fortune)
        holder.getSaveView<TextView>(R.id.tv_lottery_analysis2_permanent_fortune)
            .setTextColor(resources.getColor(model.permanentData.fortuneColor, theme))
        holder.setText(
            R.id.tv_lottery_analysis2_permanent_without_times,
            "已${model.permanentData.withoutTimes}抽未出金"
        )
        holder.setText(
            R.id.tv_lottery_analysis2_permanent_starts5_per,
            model.permanentData.averageTimes
        )
        holder.setText(R.id.tv_lottery_analysis2_permanent_times, model.permanentData.awardTimes)
        holder.setText(
            R.id.tv_lottery_analysis2_permanent_starts5,
            model.permanentData.starts5Times
        )
        addAdapter(
            R.id.rv_lottery_analysis2_permanent_content,
            model.permanentData.legendData,
            permanentAdapter
        )
    }

    private fun addAdapter(
        rvId: Int,
        data: MutableList<LotteryAnalysisModel2.DataEntity>,
        adapter: MyAdapter
    ) {
        val rvContent = holder.findView<RecyclerView>(rvId)
        rvContent.layoutManager = object : GridLayoutManager(this, 6) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        adapter.recyclerView = rvContent
        rvContent.adapter = adapter

        adapter.animationEnable = false

        adapter.setNewInstance(data)
    }

    override fun initListener() {

    }

    override fun initClient() {

    }
}

class MyAdapter(val mContext: LotteryAnalysis2Activity) :
    BaseQuickAdapter<LotteryAnalysisModel2.DataEntity, BaseViewHolder>(R.layout.item_lottery_analysis2) {

    private val permanentArray = arrayOf(
        "刻晴", "莫娜", "七七", "迪卢克", "琴",//角色
        //武器
        "阿莫斯之弓", "天空之翼", "四风原典",
        "天空之卷", "和璞鸢", "天空之脊",
        "狼的末路", "天空之傲", "天空之刃", "风鹰剑"
    )

    override fun convert(holder: BaseViewHolder, item: LotteryAnalysisModel2.DataEntity) {

        val ivHeader = holder.getView<ImageView>(R.id.iv_lottery_analysis2_item_header)

        val headerEntity = HeaderUtil.instance.getHeaderEntity(item.name)
        if (headerEntity == null) {
            ivHeader.setImageResource(R.mipmap.icon_paimeng)
            holder.setText(
                R.id.tv_lottery_analysis2_item_times,
                "${item.name}(${item.distanceCount})"
            )
        } else {
            if (headerEntity.path.isNotEmpty()) {
                ivHeader.setImageURI(Uri.parse(headerEntity.path))
            } else {
                GlideUtil.load(mContext,ivHeader,headerEntity.url)
            }
            val name =
                if (headerEntity.nickname.isEmpty()) headerEntity.name else headerEntity.nickname
            holder.setText(R.id.tv_lottery_analysis2_item_times, "${name}(${item.distanceCount})")
        }

        holder.setGone(R.id.fl_lottery_analysis2_item_type, permanentArray.contains(item.name))
    }
}

