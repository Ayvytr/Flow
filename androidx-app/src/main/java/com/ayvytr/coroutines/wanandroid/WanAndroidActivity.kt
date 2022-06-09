package com.ayvytr.coroutines.wanandroid

import android.os.Bundle
import com.ayvytr.coroutines.R
import com.ayvytr.flow.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class WanAndroidActivity: BaseActivity<WanAndroidViewModel>() {
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setContentView(R.layout.activity_wan_android)
    }
    override fun initData(savedInstanceState: Bundle?) {
        viewModel.getWanAndroidHome(
            {
                tv_value.text = it.toString()
            })

    }
}