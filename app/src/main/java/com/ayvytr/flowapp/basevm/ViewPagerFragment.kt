package com.ayvytr.flowapp.basevm

import android.os.Bundle
import com.ayvytr.flow.BaseFragment
import com.ayvytr.flow.base.IView
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.flowapp.R
import kotlinx.android.synthetic.main.fragment_lazy.*

/**
 * @author Administrator
 */
class ViewPagerFragment: BaseFragment<BaseViewModel<IView>>() {
    override fun getContentViewRes(): Int {
        return R.layout.fragment_lazy
    }

    override fun initData(savedInstanceState: Bundle?) {
        tv.text = "加载完成"
    }

}