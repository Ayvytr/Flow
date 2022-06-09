package com.ayvytr.coroutines.main

import android.os.Bundle
import com.ayvytr.coroutines.R
import com.ayvytr.flow.BaseFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Administrator
 */
class MainFragment: BaseFragment<MainViewModel>() {
    override fun getContentViewRes(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
        btn_get_data.setOnClickListener {
            tv_error.text = null
            //基本测试
            viewModel.getAndroidPostFlow(
                false,
                {
                    tv_value.text = it.toString()
                })
        }
    }
}