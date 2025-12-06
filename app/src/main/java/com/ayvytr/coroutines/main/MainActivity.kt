package com.ayvytr.coroutines.main

import android.os.Bundle
import com.ayvytr.coroutines.R
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.flow.BaseActivity
import com.ayvytr.flow.ResponseObserver
import com.ayvytr.flow.exception.NetworkException
import com.ayvytr.flow.internal.IView
import com.ayvytr.ktx.ui.show
import com.ayvytr.logger.L
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {


    override fun showLoading(isShow: Boolean) {
        super.showLoading(isShow)
    }

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
    }

    override fun initData(savedInstanceState: Bundle?) {
        btn_get_data.setOnClickListener {
            tv_error.text = null
            //基本测试
//            viewModel.getAndroidPostFlow(
//                false,
//                {
//                    tv_value.text = it.toString()
//                })

            viewModel.getAndroidPostFlow(
                false,
                {
                    tv_value.text = it.toString()
                }, {
                    tv_error.text = "未显示toast，错误： \n ${it.message}"
                })


            //重试测试
//            viewModel.getAndroidPostFlow(
//                true,
//                {
//                    tv_value.text = it.toString()
//                })


            //使用接口测试
//            viewModel.getAndroidPostInterface(object: ResponseObserver<BaseGank> {
//                override fun onSuccess(t: BaseGank) {
//                    tv_value.text = t.toString()
//                }
//
//                override fun onError(view: IView?, e: NetworkException) {
//                    super.onError(view, e)
//                    tv_error.text = "toast和textview都显示错误： \n ${e.message}"
//                }
//            })

        }
    }

    override fun onStop() {
        super.onStop()
        L.e()
    }

    override fun onDestroy() {
        super.onDestroy()
        L.e()
    }
}
