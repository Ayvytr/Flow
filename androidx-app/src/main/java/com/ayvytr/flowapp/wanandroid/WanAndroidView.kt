package com.ayvytr.flowapp.wanandroid

import com.ayvytr.flow.base.IView
import com.ayvytr.flowapp.bean.WanAndroidHome

/**
 * @author Administrator
 */
interface WanAndroidView: IView {
    fun showWanAndroidHome(wanAndroidHome: WanAndroidHome)
    fun onWanAndroidHomeFailed()
}