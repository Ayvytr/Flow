package com.ayvytr.coroutines.main

import com.ayvytr.coroutines.api.Api
import com.ayvytr.coroutines.bean.BaseGank
import com.ayvytr.flow.ResponseObserver
import com.ayvytr.flow.exception.NetworkException
import com.ayvytr.flow.vm.BaseViewModel
import com.ayvytr.network.ApiClient

/**
 * @author EDZ
 */
class MainViewModel : BaseViewModel() {
    val api = ApiClient.create(Api::class.java)


    fun getAndroidPostFlow(
        retry: Boolean,
        success: (BaseGank) -> Unit,
        error: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow({ api.getAndroidGankSuspend() }, true, retry, true, success, error)
    }

    fun getAndroidPostInterface(observer: ResponseObserver<BaseGank>) {
        launchFlow({ api.getAndroidGankSuspend() }, true, false, true, observer)
    }

}
