package cn.yooking.genshin.view

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yooking.genshin.BaseActivity
import cn.yooking.genshin.R
import cn.yooking.genshin.utils.BigDecimalUtil
import cn.yooking.genshin.utils.ShotUtil
import cn.yooking.genshin.view.model.LotteryAnalysisModel
import cn.yooking.genshin.widget.echarts.entity.Series
import cn.yooking.genshin.widget.echarts.helper.EChartsHelper
import cn.yooking.genshin.widget.echarts.view.EChartsView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Created by yooking on 2021/9/27.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class LotteryAnalysisActivity : BaseActivity() {

    private val model = LotteryAnalysisModel()

    private val adapter: BaseQuickAdapter<String, BaseViewHolder> =
        object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_lottery_analysis) {
            override fun convert(holder: BaseViewHolder, item: String) {
                val eChartsView = holder.getView<EChartsView>(R.id.ecv_lottery_analysis_echarts)
                val tvStars4 = holder.getView<TextView>(R.id.tv_lottery_analysis_stars4)
                val tvStars5 = holder.getView<TextView>(R.id.tv_lottery_analysis_stars5)
                val tvBase = holder.getView<TextView>(R.id.tv_lottery_analysis_base)
                val entity: LotteryAnalysisModel.DataEntity = when (item) {
                    "all" -> {
                        val pieSeries = EChartsHelper.getPieSeries(
                            titles = arrayOf("新手卡池", "常驻卡池", "角色卡池", "武器卡池"),
                            values = intArrayOf(
                                model.dataNoviceEntity.getSize(),
                                model.dataPermanentEntity.getSize(),
                                model.dataUpEntity.getSize(),
                                model.dataArmsEntity.getSize()
                            )
                        )

                        eChartsView.addDefWebClient(
                            EChartsHelper.getOptionString(
                                "抽卡统计",
                                pieSeries
                            )
                        )

                        tvBase.visibility = View.GONE

                        model.dataAllEntity
                    }
                    "100" -> {
                        setECharts(eChartsView, "新手卡池统计", item)
                        tvBase.visibility = View.GONE

                        model.dataNoviceEntity
                    }
                    "200" -> {
                        setECharts(eChartsView, "常驻卡池统计", item)
                        tvBase.visibility = View.VISIBLE

                        model.dataPermanentEntity
                    }
                    "301" -> {
                        setECharts(eChartsView, "角色卡池统计", item)
                        tvBase.visibility = View.VISIBLE

                        model.dataUpEntity
                    }
                    "302" -> {
                        setECharts(eChartsView, "武器卡池统计", item)
                        tvBase.visibility = View.VISIBLE

                        model.dataArmsEntity
                    }
                    else -> null
                } ?: return

                updateText(
                    tvStars4, tvStars5, intArrayOf(
                        entity.getStars4(),
                        entity.getStars4Arms(),
                        entity.getStars4Role(),
                        entity.getStars5(),
                        entity.getStars5Arms(),
                        entity.getStars5Role()
                    ),
                    entity.getSize(), item == "301"
                )

                setBaseText(tvBase, entity.getLastStars5Index())
            }

            private fun setECharts(eChartsView: EChartsView, title: String, type: String) {
                val dataEntity = when (type) {
                    "100" -> model.dataNoviceEntity
                    "200" -> model.dataPermanentEntity
                    "301" -> model.dataUpEntity
                    "302" -> model.dataArmsEntity
                    else -> model.dataAllEntity
                }

                val stars3 = dataEntity.getStars3()
                val stars4Arms = dataEntity.getStars4Arms()
                val stars4Role = dataEntity.getStars4Role()
                val stars5Arms = dataEntity.getStars5Arms()
                val stars5Role = dataEntity.getStars5Role()

                val pieSeries: Series = when (type) {
                    "301" -> {
                        EChartsHelper.getPieSeries(
                            titles = arrayOf("3星", "4星(武)", "4星(角)", "5星(角)"),
                            values = intArrayOf(
                                stars3,
                                stars4Arms,
                                stars4Role,
                                stars5Role
                            )
                        )
                    }
                    else -> {
                        EChartsHelper.getPieSeries(
                            titles = arrayOf("3星", "4星(武)", "4星(角)", "5星(武)", "5星(角)"),
                            values = intArrayOf(
                                stars3,
                                stars4Arms,
                                stars4Role,
                                stars5Arms,
                                stars5Role
                            )
                        )
                    }
                }

                eChartsView.addDefWebClient(
                    EChartsHelper.getOptionString(
                        title,
                        pieSeries
                    )
                )
            }

            private fun updateText(
                tvStars4: TextView,
                tvStars5: TextView,
                starsNum: IntArray,
                dataSize: Int,
                isUp: Boolean = false
            ) {
                val stars4Str = "4星${starsNum[0]}次(${
                    BigDecimalUtil.percentage(
                        starsNum[0],
                        dataSize
                    )
                })" + "\n - 角色：${starsNum[2]}次，武器：${starsNum[1]}次"

                val stars5Str = "5星${starsNum[3]}次(${
                    BigDecimalUtil.percentage(
                        starsNum[3],
                        dataSize
                    )
                })" + "\n - 平均1/${BigDecimalUtil.divide(dataSize, starsNum[3])}抽" +
                        if (isUp) ""
                        else "\n - 角色：${starsNum[5]}次，武器：${starsNum[4]}次"

                tvStars4.text = stars4Str
                tvStars5.text = stars5Str
            }

            private fun setBaseText(tvBase: TextView, baseNum: Int) {
                val strStart = "保底累计 "
                val spannableString = SpannableString("$strStart${baseNum}抽")
                spannableString.setSpan(
                    ForegroundColorSpan(Color.BLUE),
                    strStart.length,
                    strStart.length + "$baseNum".length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvBase.text = spannableString
            }
        }

    override fun initLayoutId(): Int {
        return R.layout.activity_lottery_analysis
    }

    override fun initData() {
        val uid = intent.getStringExtra("uid");

        //读取本地数据
        model.initData(uid)
    }

    override fun initView() {
        val rvContent = holder.findView<RecyclerView>(R.id.rv_lottery_analysis_content)
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter.recyclerView = rvContent
        rvContent.adapter = adapter

        adapter.animationEnable = false

        adapter.setNewInstance(mutableListOf("all", "301", "200", "302", "100"))
    }

    override fun initListener() {
    }

    override fun initClient() {

    }
}