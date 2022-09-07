package com.ayvytr.flowapp.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ayvytr.flowapp.R
import com.ayvytr.flowapp.lazy.LazyLoadActivity
import com.ayvytr.flowapp.basevm.ViewPagerActivity
import com.ayvytr.flowapp.wanandroid.WanAndroidActivity
import com.ayvytr.flowapp.wanandroid.WanAndroidListActivity
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

        btn_lazy.onClick {
            startActivity(Intent(getContext(), LazyLoadActivity::class.java))
        }

        btn_vp.onClick {
            startActivity(Intent(getContext(), ViewPagerActivity::class.java))
        }

        L.e(this.javaClass.genericSuperclass)
    }

    override fun onStop() {
        super.onStop()
    }
}