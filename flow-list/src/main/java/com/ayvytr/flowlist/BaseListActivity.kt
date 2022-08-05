package com.ayvytr.flowlist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayvytr.baseadapter.EmptyAdapter
import com.ayvytr.flow.BaseActivity
import com.ayvytr.flow.BaseConfig
import com.ayvytr.flow.base.IView
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.ktx.ui.getContext
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * 支持下拉刷新和上拉加载的列表Activity，数据类是[B]，下拉刷新重写[onRefreshCallback], 上拉加载重写
 * [onLoadMoreCallback], 使用[setAdapter]设置适配器，刷新成功调用[updateData]，刷新失败调用
 * [updateDataFailed]，[initData]中可调用[autoRefresh]开始下拉刷新动画
 *
 * 如果需要自定义初始化[recyclerView]和[smartRefreshLayout]，请重写[initView].
 *
 * 可通过[showLoading]，或者[BaseViewModel.launchFlow]，[BaseViewModel.zipFlow]控制是否显示loading
 *
 * @see SmartRefreshLayout
 * @see EmptyAdapter
 * @see BaseViewModel
 *
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.4
 */
abstract class BaseListActivity<T: BaseViewModel<IView>, B>: BaseActivity<T>() {
    //第一页的页标
    protected var FIRST_PAGE = 1

    lateinit var recyclerView: RecyclerView
    lateinit var smartRefreshLayout: SmartRefreshLayout

    //当前页的页数
    protected var currentPage = FIRST_PAGE

    //每页条目数
    protected var pageSize = BaseConfig.PAGE_SIZE

    private lateinit var adapter: EmptyAdapter<B>

    override fun initView(savedInstanceState: Bundle?) {
        recyclerView = findViewById(R.id.recycler_view)
        smartRefreshLayout = findViewById(R.id.smart_refresh_layout)

        recyclerView.layoutManager = LinearLayoutManager(getContext())
        smartRefreshLayout.setOnRefreshLoadMoreListener(object: OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                currentPage++
                onLoadMoreCallback(refreshLayout)
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                currentPage = FIRST_PAGE
                onRefreshCallback(refreshLayout)
            }
        })
    }

    protected abstract fun onRefreshCallback(refreshLayout: RefreshLayout)

    protected open fun onLoadMoreCallback(refreshLayout: RefreshLayout) {

    }

    protected fun enableRefresh(enable: Boolean) {
        smartRefreshLayout.setEnableRefresh(enable)
    }

    protected fun enableLoadMore(enable: Boolean) {
        smartRefreshLayout.setEnableLoadMore(enable)
    }

    protected fun finishRefresh() {
        smartRefreshLayout.finishRefresh()
    }

    protected fun finishLoadMore() {
        smartRefreshLayout.finishLoadMore()
    }

    protected fun setAdapter(adapter: EmptyAdapter<B>) {
        this.adapter = adapter
        recyclerView.adapter = this.adapter
    }

    /**
     * 更新数据，page==totalPage时，禁用上拉加载.
     */
    protected fun updateData(list: List<B>?, page: Int, totalPage: Int) {
        updateData(list, page, totalPage > page)
    }

    /**
     * 更新数据，haveMoreData=false时，禁用上拉加载.
     */
    protected fun updateData(list: List<B>?, page: Int, haveMoreData: Boolean) {
        currentPage = page
        var l = list
        if (l == null) {
            l = emptyList<B>()
        }
        if (currentPage == FIRST_PAGE) {
            adapter.updateList(l)
        } else {
            adapter.add(l)
        }
        finishRefreshAndLoadMore()
        enableLoadMore(haveMoreData)
    }

    /**
     * 更新数据失败，停止刷新
     */
    @SuppressLint("NotifyDataSetChanged")
    protected fun updateDataFailed() {
        if (currentPage == FIRST_PAGE) {
            adapter.notifyDataSetChanged()
            enableLoadMore(false)
        } else {
            enableLoadMore(true)
        }
        smartRefreshLayout.finishRefresh(false)
        smartRefreshLayout.finishLoadMore(false)
        if (currentPage > FIRST_PAGE) {
            currentPage--
        }
    }

    protected fun autoRefresh() {
        smartRefreshLayout.autoRefresh()
    }

    protected fun finishRefreshAndLoadMore() {
        finishRefresh()
        finishLoadMore()
    }

    override fun onDestroy() {
        finishRefreshAndLoadMore()
        super.onDestroy()
    }
}