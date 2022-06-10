package com.ayvytr.flow.vm

import androidx.lifecycle.ViewModel
import com.ayvytr.flow.BaseConfig
import com.ayvytr.flow.base.IView
import com.ayvytr.flow.exception.NetworkException
import com.ayvytr.flow.observer.ErrorObserver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * [BaseViewModel].
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.1
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

    /**
     * 注意：如果实现了[onError]，就不会调用[IView.showMessage]，需要重写并保留错误提示请使用[ErrorObserver]
     * 重载
     */
    fun <T> launchFlow(
        request: suspend () -> T,
        onSuccess: (T) -> Unit,
        showLoading: Boolean = true,
        retry: Boolean = false,
        repeatSameJob: Boolean = false,
        onError: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow(request, onSuccess, showLoading, retry, repeatSameJob, object: ErrorObserver {
            override fun onError(view: IView?, e: NetworkException) {
                onError?.apply {
                    onError.invoke(e)
                } ?: super.onError(view, e)
            }
        })
    }


    /**
     * [launch]和[flow]实际在这里执行.
     *
     * [showLoading]：是否显示loading（比如LoadingDialog)，默认true
     * [retry]: 接口返回错误后是否重试，默认false
     * [repeatSameJob]: 是否重复请求相同签名[request.javaClass.name]的接口，默认false
     * [onError]: 默认显示toast. 注意重载：
     *      object: ErrorObserver {} 是方便重写的，可保留原错误提示并增加更多内容
     *      ((NetworkException) -> Unit)? 是方便覆盖的，如果不为空，覆盖默认错误显示逻辑
     *
     * onCompletion比invokeOnCompletion先调用
     * JobCancellationException走onCompletion()，不走catch()
     * [request.javaClass.name]:请求签名，类似com.ayvytr.coroutines.main.MainViewModel$getAndroidPostFlow$1
     *
     * 注意：
     *  retry的逻辑不能放到[flowOn]之后[collect]之前，不然不生效
     *  两个[flowOn]中间的代码不能抽取方法，不然异常捕获不到
     *
     */
    @JvmOverloads
    fun <T> launchFlow(
        request: suspend () -> T,
        onSuccess: (T) -> Unit,
        showLoading: Boolean = true,
        retry: Boolean = false,
        repeatSameJob: Boolean = false,
        onError: ErrorObserver? = object: ErrorObserver {}
    ) {
        //requestKey代表调用到当前位置，毫秒数代表具体的某一次调用
        val requestKey = request.javaClass.name
        val key = "$requestKey:${System.currentTimeMillis()}"

        synchronized(this) {
            if (repeatSameJob && jobList.any { it.first.contains(requestKey) }) {
                return
            }
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
                onError?.onError(view, BaseConfig.networkExceptionConverter.invoke(it))
            }.flowOn(Dispatchers.Main)
                .collect {
                    onSuccess.invoke(it)
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