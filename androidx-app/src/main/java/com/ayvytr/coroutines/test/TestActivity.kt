package com.ayvytr.coroutines.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ayvytr.coroutines.R
import com.ayvytr.coroutines.wanandroid.WanAndroidActivity
import com.ayvytr.coroutines.wanandroid.WanAndroidListActivity
import com.ayvytr.ktx.ui.getContext
import com.ayvytr.ktx.ui.onClick
import com.ayvytr.logger.L
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity: AppCompatActivity() {
    //    val viewModel:TestViewModel by lazy {
//        ViewModelProvider(this).get(TestViewModel::class.java)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//        viewModel.show()
        btn_goto_wan_android.setOnClickListener {
            startActivity(Intent(getContext(), WanAndroidActivity::class.java))
        }
        btn_goto_wan_android_list.onClick {
            startActivity(Intent(getContext(), WanAndroidListActivity::class.java))
        }

        L.e(this.javaClass.genericSuperclass)
    }

    override fun onStop() {
        super.onStop()
    }
}