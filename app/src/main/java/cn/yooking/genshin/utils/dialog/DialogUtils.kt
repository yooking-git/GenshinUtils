package cn.yooking.genshin.utils.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View

/**
 * AlertDialog拓展工具
 * Created by yooking on 2021/9/22.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
const val TAG_LEFT = -1
const val TAG_CENTER = 0
const val TAG_RIGHT = 1

/**
 * 创建通用dialog
 * @param context 对象
 * @param title 标题
 * @param content 内容
 */
fun createDialog(context: Context, title: String, content: String = ""): AlertDialog {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title).setMessage(content).setCancelable(false)
    return builder.create()
}

/**
 * 创建通用自定义View dialog
 * @param context 对象
 * @param title 标题
 * @param view 内容
 */
fun createDialog(context: Context, title: String, view: View): AlertDialog {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title).setView(view).setCancelable(false)
    return builder.create()
}

fun createChoiceDialog(
    context: Context,
    title: String,
    defSelected:Int = 0,
    array: Array<String>,
    listener: DialogInterface.OnClickListener
): AlertDialog {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title).setCancelable(false)
    builder.setSingleChoiceItems(array, defSelected, listener)
    return builder.create()
}

/**
 * 添加按钮及点击事件
 * @param tag           类型 {@link DialogUtils#TAG_LEFT}
 *                          ,{@link DialogUtils#TAG_CENTER}
 *                          ,{@link DialogUtils#TAG_RIGHT}
 * @param text          按钮名称
 * @param clickListener 点击事件
 */
fun AlertDialog.addListener(
    tag: Int,
    text: String,
    clickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ ->
        dismiss()
    }
): AlertDialog {
    when (tag) {
        TAG_LEFT -> {
            setButton(DialogInterface.BUTTON_NEUTRAL, text, clickListener)
        }
        TAG_CENTER -> {
            setButton(DialogInterface.BUTTON_NEGATIVE, text, clickListener)
        }
        TAG_RIGHT -> {
            setButton(DialogInterface.BUTTON_POSITIVE, text, clickListener)
        }
    }
    return this
}