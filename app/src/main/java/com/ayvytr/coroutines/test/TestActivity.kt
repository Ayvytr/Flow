package com.ayvytr.coroutines.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ayvytr.coroutines.R
import com.ayvytr.coroutines.dialog.LoadingDialog
import com.ayvytr.coroutines.main.MainActivity
import com.ayvytr.flow.BaseConfig
import com.ayvytr.ktx.ui.getContext
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity: AppCompatActivity() {
    //    val viewModel:TestViewModel by lazy {
//        ViewModelProvider(this).get(TestViewModel::class.java)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//        viewModel.show()
        btn_goto_main.setOnClickListener {
            startActivity(Intent(getContext(), MainActivity::class.java))
        }

        BaseConfig.onShowLoading(this, true)
    }

    override fun onStop() {
        super.onStop()
        BaseConfig.onShowLoading(this, false)
    }
}