package com.ayvytr.flowapp.basevm

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.ayvytr.flow.BaseActivity
import com.ayvytr.flow.base.IView
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.flowapp.R
import kotlinx.android.synthetic.main.activity_vp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewPagerActivity: BaseActivity<BaseViewModel<IView>>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vp)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val list = listOf(ViewPagerFragment(), ViewPagerFragment(), ViewPagerFragment())
        vp.adapter = object: FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return list.size
            }

            override fun getItem(position: Int): Fragment {
                return list[position]
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return position.toString()
            }
        }
    }


    override fun initData(savedInstanceState: Bundle?) {
        viewModel.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                showMessage("delayed 1s !!!")
            }
        }
    }
}