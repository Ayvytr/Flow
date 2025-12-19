package com.ayvytr.coroutines.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBindings
import com.ayvytr.coroutines.R
import com.ayvytr.coroutines.databinding.ActivityTestBinding
import com.ayvytr.coroutines.main.MainActivity
import com.ayvytr.coroutines.viewBinding
import com.ayvytr.flow.BaseConfig
import com.ayvytr.ktx.ui.getContext

class TestActivity: AppCompatActivity() {
    //    val viewModel:TestViewModel by lazy {
//        ViewModelProvider(this).get(TestViewModel::class.java)
//    }
    private val binding by viewBinding<ActivityTestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//        viewModel.show()
        binding.apply {

            btnGotoMain.setOnClickListener {
            startActivity(Intent(getContext(), MainActivity::class.java))
        }

//        BaseConfig.onShowLoading(this, true)
        }
    }

    override fun onStop() {
        super.onStop()
//        BaseConfig.onShowLoading(this, false)
    }
}