package com.ayvytr.flowapp.wanandroid

import android.content.Context
import com.ayvytr.baseadapter.EmptyAdapter
import com.ayvytr.baseadapter.ViewHolder
import com.ayvytr.flowapp.R
import com.ayvytr.flowapp.bean.WanAndroidData

/**
 * @author Administrator
 */
class WanAndroidAdapter(context: Context):
    EmptyAdapter<WanAndroidData>(context, R.layout.item_wanandroid, R.layout.layout_empty) {
    override fun onBindView(
        holder: ViewHolder,
        t: WanAndroidData,
        position: Int,
        payloads: List<Any>
    ) {
        holder.setText(R.id.tv_title, t.title)
        holder.setText(R.id.tv_content, t.niceDate)
    }

    override fun onBindEmptyView(holder: ViewHolder) {
    }
}