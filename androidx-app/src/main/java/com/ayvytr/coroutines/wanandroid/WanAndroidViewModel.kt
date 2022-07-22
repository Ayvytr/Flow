package com.ayvytr.coroutines.wanandroid

import com.ayvytr.coroutines.App
import com.ayvytr.coroutines.api.WanAndroidApi
import com.ayvytr.coroutines.bean.MergedWanAndroid
import com.ayvytr.coroutines.bean.WanAndroidHome
import com.ayvytr.flow.observer.ErrorObserver
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.network.ApiClient

/**
 * @author Administrator
 */
class WanAndroidViewModel: BaseViewModel() {
    val wanAndroidApi = ApiClient.getRetrofit(App.WAN_ANDROID_BASE_URL)
        .create(WanAndroidApi::class.java)

    fun getWanAndroidHome(
        success: (WanAndroidHome) -> Unit,
        error: ErrorObserver? = null
    ) {
        launchFlow({ wanAndroidApi.getHomeArticle() }, success, onError = error)
    }

    fun mergeFLow(success: (MergedWanAndroid) -> Unit) {
        zipFlow(
            { wanAndroidApi.getHomeArticle() },
            { wanAndroidApi.getHotKey() },
            { a, b -> MergedWanAndroid(a, b) },
            success
        )
    }
}