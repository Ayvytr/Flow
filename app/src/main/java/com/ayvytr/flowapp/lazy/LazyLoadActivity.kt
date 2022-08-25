package com.ayvytr.flowapp.lazy

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.ayvytr.flow.BaseActivity
import com.ayvytr.flowapp.R
import com.ayvytr.flowapp.wanandroid.WanAndroidViewModel
import kotlinx.android.synthetic.main.activity_lazy_load.*

class LazyLoadActivity: BaseActivity<WanAndroidViewModel>() {
    val fragments = listOf(LazyFragment(), LazyFragment2())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lazy_load)
        vp.adapter = object:
            FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return fragments[position]::class.simpleName
            }
        }
    }
}