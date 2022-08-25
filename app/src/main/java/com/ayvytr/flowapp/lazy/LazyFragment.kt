package com.ayvytr.flowapp.lazy

import android.os.Bundle
import com.ayvytr.flow.BaseFragment
import com.ayvytr.flowapp.R
import com.ayvytr.flowapp.wanandroid.WanAndroidViewModel
import kotlinx.android.synthetic.main.fragment_lazy.*

/**
 * @author Administrator
 */
class LazyFragment: BaseFragment<WanAndroidViewModel>() {
    override fun getContentViewRes(): Int {
        return R.layout.fragment_lazy
    }

    override fun initData(savedInstanceState: Bundle?) {
        tv.text = "加载完成"
    }
}