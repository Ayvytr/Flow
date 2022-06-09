package com.ayvytr.coroutines.main

import android.os.Bundle
import com.ayvytr.coroutines.R
import com.ayvytr.flow.BaseActivity

class MainTestFragmentActivity: BaseActivity<MainViewModel>() {
    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main_test_fragment)
        setTitle("测试MainFragment")
    }
}