package com.ayvytr.coroutines.wanandroid

import android.os.Bundle
import com.ayvytr.coroutines.R
import com.ayvytr.coroutines.bean.WanAndroidHome
import com.ayvytr.flow.BaseActivity
import kotlinx.android.synthetic.main.activity_wan_android.*

class WanAndroidActivity: BaseActivity<WanAndroidViewModel>(), WanAndroidView {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setContentView(R.layout.activity_wan_android)
    }
    override fun initData(savedInstanceState: Bundle?) {
        viewModel.getWanAndroidHome()
//        viewModel.getWanAndroidHome(
//            {
//                tv_value.text = it.toString()
//            })
//        viewModel.mergeFLow {
//            tv_value.text = it.toString()
//        }

    }

    override fun showWanAndroidHome(wanAndroidHome: WanAndroidHome) {
        tv_value.text = wanAndroidHome.toString()
    }

    override fun onWanAndroidHomeFailed() {
    }
}