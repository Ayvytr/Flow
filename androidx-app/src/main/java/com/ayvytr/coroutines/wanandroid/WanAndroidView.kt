package com.ayvytr.coroutines.wanandroid

import com.ayvytr.coroutines.bean.WanAndroidHome
import com.ayvytr.flow.base.IView

/**
 * @author Administrator
 */
interface WanAndroidView: IView {
    fun showWanAndroidHome(wanAndroidHome: WanAndroidHome)
}