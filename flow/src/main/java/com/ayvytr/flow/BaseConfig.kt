package com.ayvytr.flow

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.IntRange
import com.ayvytr.flow.exception.NetworkException

/**
 * 全局配置类，便于全局修改网络请求重试次数，网络异常转换，[BaseActivity]和[BaseFragment]显示loading和错误
 * 提示方式.
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.1
 */
object BaseConfig {
    /**
     * 全局网络请求重试次数
     */
    @IntRange(from = 1, to = 10)
    @JvmField
    var networkRetryCount: Int = 2

    /**
     * 全局网络异常转换[NetworkException]，便于自定义错误提示/国际化.
     */
    @JvmField
    var networkExceptionConverter: (Throwable) -> NetworkException = { e ->
        NetworkException(e, R.string.cannot_connect_server)
    }

    /**
     * 全局显示/隐藏loading.
     * 注意：如果显示Dialog，注意内存泄漏问题
     */
    @JvmField
    var onShowLoading: (Context, Boolean) -> Unit = { context, isShow -> }

    /**
     * 全局错误提示，默认显示[Toast]
     */
    @JvmField
    var onShowMessage: (Context, CharSequence, View) -> Unit = { context, message, _ ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}