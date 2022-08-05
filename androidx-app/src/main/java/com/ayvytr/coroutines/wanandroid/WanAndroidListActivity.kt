package com.ayvytr.coroutines.wanandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.ayvytr.baseadapter.MultiItemTypeAdapter
import com.ayvytr.coroutines.R
import com.ayvytr.coroutines.bean.WanAndroidData
import com.ayvytr.coroutines.bean.WanAndroidHome
import com.ayvytr.flowlist.BaseListActivity
import com.ayvytr.ktx.ui.getContext
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * @author Administrator
 */
class WanAndroidListActivity: BaseListActivity<WanAndroidViewModel, WanAndroidData>(),
    WanAndroidView {
    val wanAndroidAdapter = WanAndroidAdapter(getContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
    }


    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        FIRST_PAGE = 0

        setAdapter(wanAndroidAdapter)
        recyclerView.addItemDecoration(DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL))
        wanAndroidAdapter.onItemClickListener = object: MultiItemTypeAdapter.OnItemClickListener<WanAndroidData>{
            override fun onItemClick(
                holder: RecyclerView.ViewHolder,
                t: WanAndroidData,
                position: Int
            ) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(t.link)))
            }
        }


        autoRefresh()
    }

    override fun onRefreshCallback(refreshLayout: RefreshLayout) {
        viewModel.getWanAndroidHome(FIRST_PAGE)
    }

    override fun onLoadMoreCallback(refreshLayout: RefreshLayout) {
        viewModel.getWanAndroidHome(currentPage + 1)
    }

    override fun showWanAndroidHome(wanAndroidHome: WanAndroidHome) {
        updateData(
            wanAndroidHome.data.datas,
            wanAndroidHome.data.curPage,
            wanAndroidHome.data.pageCount
        )
    }

    override fun onWanAndroidHomeFailed() {
        finishRefresh()
    }
}