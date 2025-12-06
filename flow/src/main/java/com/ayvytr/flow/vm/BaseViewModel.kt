package com.ayvytr.flow.vm

import androidx.lifecycle.ViewModel
import com.ayvytr.flow.BaseConfig
import com.ayvytr.flow.ResponseObserver
import com.ayvytr.flow.exception.NetworkException
import com.ayvytr.flow.internal.IView
import com.ayvytr.flow.internal.toKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 */
open class BaseViewModel : ViewModel(), CoroutineScope by MainScope() {
    var view: IView? = null

    //job管理
    protected val jobList by lazy {
        Vector<Triple<String, Boolean, Job>>()
    }

    override fun onCleared() {
        cancelAllJob()
        cancel()
        view = null
    }

    fun <T> launchFlow(
        request: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow(request, true, false, onSuccess, onError)
    }


    fun <T> launchFlow(
        request: suspend () -> T,
        showLoading: Boolean,
        onSuccess: (T) -> Unit,
        onError: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow(request, showLoading, false, onSuccess, onError)
    }

    fun <T> launchFlow(
        request: suspend () -> T,
        showLoading: Boolean,
        retry: Boolean,
        onSuccess: (T) -> Unit,
        onError: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow(request, showLoading, retry, false, onSuccess, onError)
    }

    /**
     * 注意：如果实现了[onError]，就不会调用[IView.showMessage]，不然需要加标志量控制是否显示，麻烦
     */
    fun <T> launchFlow(
        request: suspend () -> T,
        showLoading: Boolean,
        retry: Boolean,
        removeSameJob: Boolean = false,
        onSuccess: (T) -> Unit,
        onError: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow(request, showLoading, retry, removeSameJob, object: ResponseObserver<T> {
            override fun onSuccess(t: T) {
                onSuccess(t)
            }

            override fun onError(view: IView?, e: NetworkException) {
                onError?.apply {
                    invoke(e)
                } ?: super.onError(view, e)
            }
        })
    }

    fun <T> launchFlow(
        request: suspend () -> T,
        responseObserver: ResponseObserver<T>
    ) {
        launchFlow(request, true, false, responseObserver)
    }

    fun <T> launchFlow(
        request: suspend () -> T,
        showLoading: Boolean,
        responseObserver: ResponseObserver<T>
    ) {
        launchFlow(request, showLoading, false, responseObserver)
    }

    fun <T> launchFlow(
        request: suspend () -> T,
        showLoading: Boolean,
        retry: Boolean,
        responseObserver: ResponseObserver<T>
    ) {
        launchFlow(request, showLoading, retry, false, responseObserver)
    }

    /**
     * [launch]和[flow]实际在这里执行.
     * [showLoading]：是否显示loading（比如LoadingDialog)，默认true
     * [retry]: 接口返回错误后是否重试，默认false
     * [removeSameJob]: 多次重复调用这个接口时，是否取消之前正在调用的[Job]
     * [responseObserver]: 响应处理，默认[ResponseObserver.onError]会调用[IView.showMessage]显示错误，
     *  提供这个方法而不直接使用onSuccess: (T) -> Unit, onError: (NetworkException) -> Unit的原因是可以
     *  自行控制但是默认的[IView.showMessage]是否需要调用
     *
     * onCompletion比invokeOnCompletion先调用
     * JobCancellationException走onCompletion()，不走catch()
     */
    fun <T> launchFlow(
        request: suspend () -> T,
        showLoading: Boolean = true,
        retry: Boolean = false,
        removeSameJob: Boolean = false,
        responseObserver: ResponseObserver<T>
    ) {
        //stackKey代表调用到当前位置，毫秒数代表具体的某一次调用
        val stackKey = Thread.currentThread().stackTrace[2].toKey()
        val key = "$stackKey:${System.currentTimeMillis()}"

        if (removeSameJob) {
            removeJobByStackKey(stackKey)
        }

        val job = launch {
            var flow = flow<T> {
                emit(request.invoke())
            }.flowOn(Dispatchers.IO)
                .onStart {
                    if (showLoading) {
                        //显示loading
                        view?.showLoading()
                    }
                }
            if (retry) {
                flow = flow.retry(BaseConfig.networkRetryCount.toLong())
            }
            flow.catch {
                responseObserver.onError(view, BaseConfig.networkStringIdConverter.invoke(it))
            }
//                .onCompletion {
//                    L.e("onCompletion ${if (it == null) "成功" else "失败"}", it)
//                    if (it != null && it::class.java.name != "kotlinx.coroutines.JobCancellationException") {
//                        error(it.toNetworkException())
//                    }
//                }
                .flowOn(Dispatchers.Main)
                .collect {
                    responseObserver.onSuccess(it)
                }
        }

        addJob(key, showLoading, job)

        job.invokeOnCompletion {
            removeJobByKey(key)

            if (showLoading) {
                //如果没有正在loading的接口，隐藏loading
                val isEmpty = jobList.filter { it.second }.isEmpty()
                if (isEmpty) {
                    view?.showLoading(false)
                }
            }
        }
    }

    private fun addJob(key: String, showLoading: Boolean, job: Job) {
        jobList.add(Triple(key, showLoading, job))
    }

    private fun removeJobByKey(key: String) {
        jobList.filter { it.first == key }.forEach {
            val job = it.third
            cancelJob(job)
            jobList.remove(it)
        }
    }

    private fun removeJobByStackKey(stackKey: String) {
        jobList.filter {
            it.first.contains(stackKey)
        }.forEach {
            val job = it.third
            cancelJob(job)
            jobList.remove(it)
        }
    }

    private fun cancelJob(job: Job) {
        if (job.isActive) {
            job.cancel()
        }
    }

    private fun cancelAllJob() {
        jobList.forEach {
            val job = it.third
            cancelJob(job)
            jobList.remove(it)
        }
    }
}