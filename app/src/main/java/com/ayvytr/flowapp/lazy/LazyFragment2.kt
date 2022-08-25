package com.ayvytr.flowapp.lazy

import android.os.Bundle
import com.ayvytr.flow.BaseFragment
import com.ayvytr.flowapp.R
import com.ayvytr.flowapp.bean.WanAndroidHome
import com.ayvytr.flowapp.wanandroid.WanAndroidView
import com.ayvytr.flowapp.wanandroid.WanAndroidViewModel
import kotlinx.android.synthetic.main.fragment_lazy.*

/**
 * @author Administrator
 */
class LazyFragment2: BaseFragment<WanAndroidViewModel>(), WanAndroidView {
    override fun getContentViewRes(): Int {
        return R.layout.fragment_lazy
    }

    override fun isLazyLoadEnabled(): Boolean {
        return true
    }

    override fun initView(savedInstanceState: Bundle?) {
        tv.textSize = 20F
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.getWanAndroidHome(1)
    }

    override fun showWanAndroidHome(wanAndroidHome: WanAndroidHome) {
        tv.text = wanAndroidHome.toString()
    }

    override fun onWanAndroidHomeFailed() {
        tv.text = "加载失败"
    }

}