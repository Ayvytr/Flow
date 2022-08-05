package com.ayvytr.coroutines.wanandroid

import com.ayvytr.coroutines.App
import com.ayvytr.coroutines.api.WanAndroidApi
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.network.ApiClient

/**
 * @author Administrator
 */
class WanAndroidViewModel: BaseViewModel<WanAndroidView>() {

    val wanAndroidApi = ApiClient.getRetrofit(App.WAN_ANDROID_BASE_URL)
        .create(WanAndroidApi::class.java)

//    fun getWanAndroidHome(
//        success: (WanAndroidHome) -> Unit,
//        error: ErrorObserver? = null
//    ) {
//        launchFlow({ wanAndroidApi.getHomeArticle() }, success)
//    }
//
//    fun mergeFLow(success: (MergedWanAndroid) -> Unit) {
//        zipFlow(
//            { wanAndroidApi.getHomeArticle() },
//            { wanAndroidApi.getHotKey() },
//            { a, b -> MergedWanAndroid(a, b) },
//            success
//        )
//    }

    fun getWanAndroidHome(page: Int = 0) {
        launchFlow(
            { wanAndroidApi.getHomeArticle(page) },
            { view.showWanAndroidHome(it) },
            {
                view.showMessage(it.stringId)
                view.onWanAndroidHomeFailed()
            }
        )
    }
}