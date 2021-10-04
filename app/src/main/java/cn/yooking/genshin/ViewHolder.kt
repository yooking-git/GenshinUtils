@file:Suppress("UNCHECKED_CAST")

package cn.yooking.genshin

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import cn.yooking.genshin.utils.ClickUtil


/**
 * @param view 如果为activity则不传
 * @param context Activity或Fragment对象
 */
class ViewHolder(private var context: Context, private var view: View? = null) {

    private val clickUtil = ClickUtil()

    private var viewMap: MutableMap<Int, View> = mutableMapOf()

    fun <T : View> findView(@IdRes viewId: Int): T {

        if (viewMap.containsKey(viewId)) return viewMap[viewId] as T

        val findView: View
        if (context is Activity) {
            findView = (context as Activity).findViewById(viewId)
            viewMap[viewId] = findView
        } else {
            if (view == null) throw NullPointerException("该对象非Activity，请传入view对象")
            findView = view!!.findViewById(viewId)
            viewMap[viewId] = findView
        }
        return findView as T
    }

    fun saveView(view: View) {
        viewMap[view.id] = view
    }

    fun <T : View> getSaveView(viewId: Int): T {
        return viewMap[viewId] as T
    }

    fun setOnClickListener(vararg viewIds: Int, clickListener: View.OnClickListener) {
        setOnClickListener(
            views = Array(
                viewIds.size,
                init = { findView(viewIds[it]) }
            ),
            clickListener
        )
    }

    fun setOnClickListener(vararg views: View, clickListener: View.OnClickListener) {
        views.forEach { view ->
            clickUtil.bind(view)
            view.setOnClickListener {
                if (clickUtil.clickEnable()) {
                    clickListener.onClick(view)
                }
            }
        }
    }

    fun setOnLongClickListener(vararg viewIds: Int, clickListener: View.OnLongClickListener) {
        setOnLongClickListener(
            views = Array(
                viewIds.size,
                init = { findView(viewIds[it]) }
            ),
            clickListener
        )
    }

    fun setOnLongClickListener(vararg views: View, clickListener: View.OnLongClickListener) {
        views.forEach {
            it.setOnLongClickListener(clickListener)
        }
    }

    fun setText(@IdRes viewId: Int, text: String) {
        val textView = findView<View>(viewId)
        if (textView is TextView)
            textView.text = text
    }

    fun getText(@IdRes viewId: Int): String {
        val textView = findView<View>(viewId)
        return if (textView is TextView)
            textView.text.toString()
        else
            ""
    }

    fun setTextTypeFace(@IdRes viewId: Int, typeFaceUri: String) {
        val textView = findView<View>(viewId)
        if (textView is TextView) {
            val typeface = Typeface.createFromAsset(context.assets, typeFaceUri)
            textView.typeface = typeface
        }
    }

    fun setVisibility(vararg viewIds: Int, visibility: Int) {
        viewIds.forEach {
            val view = findView<View>(it)
            view.visibility = visibility
        }
    }

    fun setVisibility(vararg views: View, visibility: Int) {
        views.forEach {
            it.visibility = visibility
        }
    }
}