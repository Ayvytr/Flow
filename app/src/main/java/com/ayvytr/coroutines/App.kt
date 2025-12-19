package com.ayvytr.coroutines

import android.app.Application
import com.ayvytr.flow.BaseConfig
import com.ayvytr.flow.exception.NetworkException
import com.ayvytr.ktx.context.isNetworkAvailable
import com.ayvytr.logger.L
import com.ayvytr.network.ApiClient
import com.ayvytr.network.isNetworkAvailable
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author admin
 */

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化，默认开启了OKhttp缓存，cache=null关闭
        ApiClient.init("https://gank.io/api/", cache = null)
        L.settings().showLog(BuildConfig.DEBUG)
        //覆盖重写自定义全局网络异常转为ResponseMessage
//        ApiClient.throwable2ResponseMessage = {
//            ResponseMessage("哈哈", throwable = it)
//        }
        BaseConfig.networkStringIdConverter = { e ->
            var exception = NetworkException(e)
            val networkAvailable = isNetworkAvailable()
            when (e) {
                is SocketTimeoutException -> {
                    if(e.message!!.startsWith("failed to connect to")) {
                        exception.stringId = R.string.cannot_connect_server
                    } else {
                        exception.stringId = R.string.network_timeout
                    }
                }
                is ConnectException       -> {
                    exception.stringId =
                        if (networkAvailable) R.string.cannot_connect_server else R.string.network_not_available
                }
                is UnknownHostException   -> {
                    exception.stringId =
                        if (networkAvailable) R.string.cannot_connect_server else R.string.network_not_available
                }
                is HttpException          -> {
                    exception = NetworkException(e, R.string.cannot_connect_server, e.code())
                }
                else                      -> {
                    NetworkException(e, R.string.other_error)
                }
            }

            exception
        }
    }
}