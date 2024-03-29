package com.ayvytr.flow.vm

import androidx.lifecycle.ViewModel
import com.ayvytr.flow.BaseConfig
import com.ayvytr.flow.base.IView
import com.ayvytr.flow.exception.NetworkException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * [BaseViewModel].
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.7
 * 增加[performShowMessage]，解决[launchFlow], [zipFlow] onError默认实现 view.showMessage(it.stringId)
 * 不适用网络请求错误的问题
 *
 * @since 0.0.3 修改[view]为protect
 * @since 0.0.2
 * 变更职能：增加[BaseViewModel]泛型[IView]，支持BaseActivity, BaseFragment重写：
 *                  1.方便接口回调写在BaseViewModel中，Activity,Fragment只做需要的ui回调
 *                  2.方便回传参数.
 *
 * 增加 [zipFlow]
 *
 * @since 0.0.1 增加[launchFlow]等基础功能
 */
open class BaseViewModel<out V: IView> : ViewModel(), CoroutineScope by MainScope() {

    protected lateinit var view: @UnsafeVariance V

    //job管理
    protected val jobList by lazy {
        Vector<Triple<String, Boolean, Job>>()
    }

    fun setIView(v: @UnsafeVariance V) {
        view = v
    }

    override fun onCleared() {
        cancelAllJob()
        cancel()
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
     *  如果实现了[onError]，就不会调用[IView.showMessage]
     *  retry的逻辑不能放到[flowOn]之后[collect]之前，不然不生效
     *  [launchFlow]的两个[flowOn]中间的代码不能抽取方法和[zipFlow]公用，不然[catch]不到异常
     */
    fun <T> launchFlow(
        request: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: (NetworkException) -> Unit = { performShowMessage(it) },
        showLoading: Boolean = true,
        retry: Boolean = false,
        repeatSameJob: Boolean = false,
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
                        view.showLoading()
                    }
                }
            if (retry) {
                flow = flow.retry(BaseConfig.networkRetryCount.toLong())
            }
            flow.catch {
                onError.invoke(BaseConfig.networkExceptionConverter.invoke(it))
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
                    view.showLoading(false)
                }
            }
        }
    }

    /**
     * 统一显示错误，网络请求返回的是字符串，本地的可以是string res.
     */
    protected open fun performShowMessage(it: NetworkException) {
        if (it.isValidStringId()) {
            view.showMessage(it.stringId)
        } else {
            view.showMessage(it.message.toString())
        }
    }


    /**
     * 通过[transform],合并[request1]请求的[T]和[request2]请求的[R]，[onSuccess]返回[OUT]
     * @see [launchFlow]
     */
    @JvmOverloads
    fun <T, R, OUT> zipFlow(
        request1: suspend () -> T,
        request2: suspend () -> R,
        transform: (T, R) -> OUT,
        onSuccess: (OUT) -> Unit,
        onError: (NetworkException) -> Unit = { performShowMessage(it) },
        showLoading: Boolean = true,
        retry: Boolean = false,
        repeatSameJob: Boolean = false,
    ) {
        //requestKey代表调用到当前位置，毫秒数代表具体的某一次调用
        val requestKey = request1.javaClass.name + "," + request2.javaClass.name
        val key = "$requestKey:${System.currentTimeMillis()}"

        synchronized(this) {
            if (repeatSameJob && jobList.any { it.first.contains(requestKey) }) {
                return
            }
        }

        val job = launch {
            var flow = flow<OUT> {
                //async{}.await()问题：catch不到404异常
//                val v1 = async { r1.invoke() }.await()
//                val v2 = async { r2.invoke() }.await()
                emit(transform(request1.invoke(), request2.invoke()))
            }.flowOn(Dispatchers.IO)
                .onStart {
                    if (showLoading) {
                        //显示loading
                        view.showLoading()
                    }
                }
            if (retry) {
                flow = flow.retry(BaseConfig.networkRetryCount.toLong())
            }
            flow.catch {
                onError.invoke(BaseConfig.networkExceptionConverter.invoke(it))
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
                    view.showLoading(false)
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