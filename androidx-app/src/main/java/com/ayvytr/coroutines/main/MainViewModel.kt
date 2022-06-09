package com.ayvytr.coroutines.main

import com.ayvytr.coroutines.api.GankApi
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.flow.exception.NetworkException
import com.ayvytr.flow.observer.ErrorObserver
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.network.ApiClient

/**
 * @author EDZ
 */
class MainViewModel : BaseViewModel() {
    val gankApi = ApiClient.create(GankApi::class.java)

    fun getAndroidPostFlow(
        retry: Boolean,
        success: (BaseGank) -> Unit,
        error: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow({ gankApi.getAndroidGankSuspend() }, success, true, retry, true, error)
    }

    fun getIosPostFlow(
        success: (BaseGank) -> Unit,
        error: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow({ gankApi.getIosGankSuspend() }, success, onError = error)
    }

    fun getAndroidPostFlowErrorObserver(
        retry: Boolean,
        success: (BaseGank) -> Unit,
        error: ErrorObserver? = null
    ) {
        launchFlow({ gankApi.getAndroidGankSuspend() }, success, true, retry, true, error)
    }

}
