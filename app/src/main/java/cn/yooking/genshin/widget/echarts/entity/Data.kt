package cn.yooking.genshin.widget.echarts.entity

import java.util.*

/**
 * Created by yooking on 2021/9/23.
 * Copyright (c) 2021 yooking. All rights reserved.
 */

//顶级
/**
 * @param title 标题
 * @param legend 图例
 * @param toolbox 工具栏
 * @param series 图表
 */
class Option(
    var title: Title,
    var legend: Legend,
    var toolbox: Toolbox,
    var series: MutableList<Series>,
    var animation: Boolean = true
) {
    constructor() : this(Title(), Legend(), Toolbox(), arrayListOf(), true)
}

/**
 * @param text 标题
 * @param show 是否展示
 */
class Title(
    var text: String,
    var left: String = "left",
    var top: String = "top",
    var show: Boolean = true
) {
    constructor() : this("", show = false)
}

/**
 * 图例
 * @param top 展示的位置 'top', 'middle', 'bottom'
 * @param left 展示的位置 'left', 'center', 'right'
 * @param orient 展示方式 'vertical','horizontal'
 * @param selected 默认是否展示{'Search Engine':false}
 * @param show 是否展示
 */
class Legend(
    var top: String = "auto",
    var left: String = "auto",
    var orient: String = "horizontal",
    var selected: Map<String, Boolean> = mapOf(),
    var show: Boolean = true
) {
    constructor() : this("bottom", "right", "vertical")
}

/**
 * 工具栏
 * @param feature 工具详情
 * @param show 是否展示
 */
class Toolbox(var feature: Feature, var show: Boolean = true) {
    constructor() : this(Feature(), false)

    /**
     * @param dataView 数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
     * @param restore 还原配置项
     * @param saveAsImage 将图表保存为图片
     */
    class Feature(
        var dataView: DataView,
        var restore: Restore,
        var saveAsImage: SaveAsImage
    ) {
        constructor() : this(
            DataView(),
            Restore(),
            SaveAsImage()
        )

        /**
         * 数据视图工具
         * @param show 是否展示
         * @param readOnly 是否只读
         */
        class DataView(var readOnly: Boolean, var show: Boolean = true) {
            constructor() : this(true, false)
        }

        /**
         * 还原配置项
         * @param show 是否展示
         */
        class Restore(var show: Boolean) {
            constructor() : this(false)
        }

        /**
         * 将图表保存为图片
         * @param show 是否展示
         */
        class SaveAsImage(var show: Boolean) {
            constructor() : this(false)
        }
    }
}

/**
 * 图表展示及数据
 * @param type 类型 - pie 饼图
 * @param name 系列名称，用于tooltip的显示，legend 的图例筛选，在 setOption 更新数据和配置项时用于指定对应的系列。
 * @param minAngle 最小角度，防止其他数据太小而消失不见
 * @param radius 第一个数字：空心半径 第二个数字：外半径 支持：百分占比(屏幕比例)/或数字（px）
 * @param center 第一个数字为横坐标，第二个数字为纵坐标 支持：百分占比(屏幕比例)/或数字（px）
 * @param roseType 是否展示成南丁格尔图，通过半径区分数据大小。可选择两种模式：<br/>
 *                  'radius' 扇区圆心角展现数据的百分比，半径展现数据的大小。<br/>
 *                  'area' 所有扇区圆心角相同，仅通过半径展现数据大小。<br/>
 *                  不传为非南丁格尔图
 * @param itemStyle 图形样式
 * @param data 数据
 */
class Series(
    var type: String,
    var name: String,
    var minAngle: Int,
    var radius: Array<String>,
    var center: Array<String>,
    var roseType: String = "",
    var itemStyle: ItemStyle,
    var label: Label,
    var data: MutableList<Data>
) {

    constructor() : this(
        "",
        "",
        0,
        arrayOf("40%", "70%"),
        arrayOf("50%", "50%"),
        "area",
        ItemStyle(),
        Label(),
        arrayListOf()
    )

    /**
     * @param borderRadius 每个饼的边角弧度
     */
    class ItemStyle(var borderRadius: Int, var borderColor: String = "#fff", borderWidth: Int = 0) {
        constructor() : this(2, "#fff", 10)
    }

    /**
     * 标签
     * @param position inside/outside
     * @param formatter 字符串模板 模板变量有：
     *                  {a}：系列名。
     *                  {b}：数据名。
     *                  {c}：数据值。
     *                  {d}：百分比。
     *                  {@xxx}：数据中名为 'xxx' 的维度的值，如 {@product} 表示名为 'product' 的维度的值。
     *                  {@[n]}：数据中维度 n 的值，如 {@[3]} 表示维度 3 的值，从 0 开始计数。}
     * @param color inherit(和图表同色)
     */
    class Label(
        var position: String = "outside",
        var formatter: String = "{b}",
        var color: String = "inherit",
        var show: Boolean = true
    ) {
        constructor() : this("outside", "{b}\\n{c}次", "inherit", true)
    }

    /**
     * 数据
     * @param name 数据名称
     * @param value 值
     * @param itemStyle 单个设置
     */
    class Data(
        var name: String,
        var value: Int,
        var itemStyle: ItemStyle = ItemStyle()
    ) {
        constructor() : this("", 0, ItemStyle())

        class ItemStyle(var color: String?) {
            constructor() : this(null)
        }
    }
}