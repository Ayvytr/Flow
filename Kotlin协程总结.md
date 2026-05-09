# Kotlin协程总结

## 协程作用域：[CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html)，协程的「容器」，负责管理协程的生命周期，防止内存泄漏

**作用**：结构化并发，取消作用域时会自动取消所有子协程



### Android常用作用域

|                |                                                              |
| -------------- | ------------------------------------------------------------ |
| GlobalScope    | 全局作用域，**不推荐使用**（生命周期与应用一致，**比 Application 还长**，易内存泄漏） |
| lifecycleScope | 绑定 Activity/Fragment 生命周期，销毁时自动取消              |
| viewModelScope | 绑定 ViewModel 生命周期，ViewModel 销毁时自动取消            |



```kotlin
//同Application作用域的方法
class MyApp : Application(), CoroutineScope {

    // 1. 定义 Job：管理整个应用的协程
    private val appJob = SupervisorJob()

    // 2. 重写协程上下文：主线程 + 应用级 Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + appJob

    // 3. 可选：封装一个应用级启动协程的方法
    fun launchAppScope(block: suspend CoroutineScope.() -> Unit) {
        launch {
            block()
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 应用启动时可以在这里执行全局协程任务
        // 示例：launchAppScope { 你的异步任务 }
    }

}

//或者方法2：MainScope() = Dispatchers.Main + SupervisorJob()，开箱即用
//CoroutineScope by MainScope() 自动实现所有接口，代码极简
class MyApp : Application(), CoroutineScope by MainScope() {

    override fun onCreate() {
        super.onCreate()
        // 直接使用 launch 启动协程
        launch {
            // 全局任务
        }
    }


}


//释放：onTerminate() 真机永不调用 → 里面写 cancel() 无意义
//取消时机可在：onLowMemory() + onTrimMemory()
class MyApp: Application(), ... {
    
//    override fun onTerminate() {
//        super.onTerminate()
//        // 应用销毁时，取消所有应用级协程（安全回收）
//        cancel()
//    }
    
        override fun onLowMemory() {
        super.onLowMemory()
        cancel() // 低内存时取消
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // 只要是后台高内存修剪，直接释放
        if (level >= TRIM_MEMORY_BACKGROUND) {
            cancel()
        }
    }
}
```



```kotlin
// 获取全局 Application 协程作用域
val appScope = (applicationContext as MyApp)

// 启动一个和应用生命周期一样长的协程
appScope.launch {
    // 异步任务（网络请求、数据库操作等）
    withContext(Dispatchers.IO) {
        // 耗时操作
    }
}

// 也可以直接用封装好的方法
appScope.launchAppScope {
    // 你的任务
}
```







### 方法列表

| 方法                                                         | 功能                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| interface [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html)(source) | 定义新协程的范围。每个协程构建器都是[CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.md)的扩展，并继承其[coroutineContext](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/coroutine-context.html) 以自动传播上下文元素和取消。 |
| CoroutineScope.isAlive                                       | 当前作业处于活动状态时返回true（未完成且未取消）             |
| actor                                                        |                                                              |
| [async](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html) | 创建一个协同程序并将其未来结果作为[Deferred](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/index.html)的实现返回。 |
| broadcast                                                    |                                                              |
| cancel                                                       | 取消此协程和所有子协程，可指定取消原因                       |
| ensureAlive                                                  |                                                              |
| launch                                                       | 在不阻塞当前线程的情况下启动新的协同程序，并将协程的引用作为[Job返回](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html)。取消生成的作业时，协程将被[取消](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/cancel.html)。 |
| newCoroutineContext                                          |                                                              |
| plus                                                         | 将指定的协程上下文添加到此作用域，使用相应的键覆盖当前作用域上下文中的现有元素。 |
| produce                                                      | 通过将新的协同程序发送到通道并将协程的引用作为[ReceiveChannel](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-receive-channel/index.html)返回，从而生成新的协同程序以生成值流。该结果对象可用于[接收](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-receive-channel/receive.html)由该协程生成的元素。 |



## CoroutineContext（协程上下文）：协程的「运行环境」，是一组 Key-Value 元素的集合：

**Job**：协程的「句柄」，管理协程生命周期（取消、等待完成）

**CoroutineDispatcher**：调度协程到指定线程（`Main`/`IO`/`Default`），决定协程在哪个线程运行

**CoroutineName**：协程名称，用于调试

**CoroutineExceptionHandler**：全局异常处理器





## 启动协程的所有方式

|                     | 作用                                               | 返回值            | 用途                                                         |
| ------------------- | -------------------------------------------------- | ----------------- | ------------------------------------------------------------ |
| launch              | 启动一个**无返回值**的协程                         | Job               | 执行后台任务、网络请求、数据库操作、计时等                   |
| async               | 启动一个**有返回值**的协程                         | Deferred<T>       | 需要**等待结果**、并发任务。必须用**.await()**获取结果       |
| produce             | 启动一个**生产者协程**                             | ReceiveChannel<T> | 连续发送多个数据。内部调用 `send()`，外部调用 `receive()`    |
| actor               | 启动一个**消费者协程**                             | SendChannel<T>    | 串行处理消息、状态管理、线程安全模型。内部调用 `receive()`，外部调用 `send()`<br />**已标记过时**，官方推荐使用更简单的协程通道（Channel）和流（Flow） |
| flow / callbackFlow | 严格说不是启动协程，但**内部会启动协程**发送数据流 |                   |                                                              |



```kotlin
//produce
fun testProduce() = runBlocking {
    // 生产者：启动协程，源源不断发数据
    val channel = produce {
        for (i in 1..3) {
            delay(300)
            send(i) // 发送数据到通道
            println("生产了: $i")
        }
    }

    // 消费者：遍历接收所有数据
    for (num in channel) {
        println("收到了: $num")
    }

    println("全部接收完毕")
}
```





## 重要方法

|                |                                                              |
| -------------- | ------------------------------------------------------------ |
| withContext    | 必须在协程内部使用，**切换协程上下文**/**切线程**，并**返回结果 |
| runBlocking    | **阻塞当前线程**直到协程结束                                 |
| coroutineScope | 创建子作用域，**不启动新协程**。                             |
| delay          | 非阻塞，延迟协程的执行。只挂起协程，不阻塞线程               |

```kotlin
withContext(Dispatchers.IO) {
    // IO 任务
}
```



NonCancellable: 不可取消的作业。专为withContext设计的执行不可取消的代码

```
withContext(NonCancellable) {
	//这里代码不可以被取消
}
```



## suspend

### 标记函数为挂起函数，只能在协程或其他挂起函数中调用

```kotlin
suspend fun fetchDataFromNetwork(): String {
    // 切换到 IO 线程执行网络请求
    return withContext(Dispatchers.IO) {
        // 模拟网络请求
        delay(1000)
        "网络数据"
    }
}
```

### 封装回调为挂起函数

```kotlin
// 原始回调式 API（车机蓝牙 SDK）
fun connectBluetooth(device: String, callback: BluetoothCallback) {
    // ... SDK 内部实现
}

// 封装为挂起函数
suspend fun connectBluetoothSuspend(device: String): Boolean {
    return suspendCancellableCoroutine { continuation ->
        // 注册取消回调
        continuation.invokeOnCancellation {
            // 协程取消时，断开蓝牙连接
            disconnectBluetooth()
        }
        
        // 调用原始 API
        connectBluetooth(device, object : BluetoothCallback {
            override fun onSuccess() {
                // 恢复协程，返回成功
                continuation.resume(true)
            }
            
            override fun onFailure(error: Exception) {
                // 恢复协程，抛出异常
                continuation.resumeWithException(error)
            }
        })
    }
}

// 使用
lifecycleScope.launch {
    try {
        val success = connectBluetoothSuspend("BYD_HAN_001")
        if (success) showToast("蓝牙连接成功")
    } catch (e: Exception) {
        showToast("蓝牙连接失败：${e.message}")
    }
}
```



## 为什么要用 `lifecycleScope`/`viewModelScope`？

**自动取消**：页面销毁时自动取消协程，避免内存泄漏

**自动绑定调度器**：默认在 `Dispatchers.Main` 启动，可直接更新 UI



## 取消的传播

**默认行为**：父协程取消 → 所有子协程取消；子协程失败 → 父协程取消

**SupervisorJob**：子协程失败不影响父协程和其他子协程（适合独立任务）



## Job类型

|                |                                                              |
| -------------- | ------------------------------------------------------------ |
| CompletableJob | 允许手动完成的 Job，可以等待完成信号。                       |
| Deferred<T>    | 有返回值的 Job                                               |
| SupervisorJob  | **最重要的一种特殊 Job**，子协程的失败不会影响父协程和其他兄弟协程。 |



## 异常处理

|        |                                                         |
| ------ | ------------------------------------------------------- |
| launch | 异常会直接抛出，需要用 `CoroutineExceptionHandler` 捕获 |
| async  | 异常会在 `await()` 时抛出，需要用 `try-catch`捕获       |

```kotlin
// 全局异常处理器
val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.e("Coroutine", "协程异常", throwable)
    // 上报错误到车机监控平台
    reportToMonitor(throwable)
}

// 使用
lifecycleScope.launch(exceptionHandler) {
    // 这里的异常会被 exceptionHandler 捕获
    throw RuntimeException("车机 SDK 异常")
}
```



[CoroutineExceptionHandler](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-exception-handler/index.html): 协程上下文中的可选元素，用于处理未捕获的异常。 通常，未捕获的异常只能来自使用[启动](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html)构建器创建的协程。使用[async](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html)创建的协程始终捕获其所有异常，并在生成的[Deferred](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/index.html)对象中表示它们。

默认情况下，如果未安装处理程序，则以下列方式处理未捕获的异常：

- 如果异常是[CancellationException](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-cancellation-exception/index.html)则忽略它（因为这是取消正在运行的协同程序的假设机制）
- 除此以外：
  - 如果上下文中有[Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html)，则调用[Job.cancel](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/cancel.html) ;
  - 否则，通过[ServiceLoader](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-exception-handler/index.html#)找到[CoroutineExceptionHandler的](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-exception-handler/index.md)所有实例
  - 并且调用当前线程的[Thread.uncaughtExceptionHandler](http://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#uncaughtExceptionHandler)。



## Dispatchers 协程调度器

Dispatchers 决定了协程在哪个线程或线程池中执行

| 属性       | 可调度任务类型                                       |
| ---------- | ---------------------------------------------------- |
| DEFAULT    | CPU密集型任务。launch, async等协程创建器的默认调度器 |
| IO         | I/O密集型任务                                        |
| Main       | UI/主线程                                            |
| Unconfined | 不强制指定线程                                       |





## 实现协程同步的元素

|                                           |                                                              |
| ----------------------------------------- | ------------------------------------------------------------ |
| 同一个协程体内的代码（天然同步）          | 一个 `launch` / `async` 代码块里，代码**从上到下顺序执行**，挂起函数不改变顺序 |
| `await()` 等待异步结果（强制同步）        | `async` 本身是并发，但调用 **`await()`** 会**挂起等待结果**，实现同步 |
| `Mutex` 互斥锁（临界区同步）              | 多协程访问共享资源时，保证同一时间只有一个协程执行。相当于线程的 `synchronized` / `Lock`，但**非阻塞、轻量**。 |
| `Job.join()` 等待协程完成                 | 协程之间的串行依赖                                           |
| **`Channel` 队列同步（生产者 - 消费者）** | 通过**通道发送 / 接收等待**，强制顺序执行。                  |

```kotlin
val mutex = Mutex()

scope.launch {
    mutex.withLock { 
        // 同一时间只有一个协程能进这里
        共享资源操作
    }
}
```



## 顶级扩展方法

| 方法                                                         | 功能                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| delay                                                        | 非阻塞睡眠                                                   |
| yield                                                        | 在单线程调度器中产生线程                                     |
| withContext                                                  | 切换到不同的上下文                                           |
| [withTimeout](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/with-timeout.html) | 设置指定的超时在协程内运行给定的代码块，如果超时，抛出TimeoutCancellationException. The code that is executing inside the [block](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/with-timeout.html#kotlinx.coroutines$withTimeout(kotlin.Long, kotlin.SuspendFunction1((kotlinx.coroutines.CoroutineScope, kotlinx.coroutines.withTimeout.T)))/block) is cancelled on timeout and the active or next invocation of the cancellable suspending function inside the block throws a [TimeoutCancellationException](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-timeout-cancellation-exception.html). |
| withTimeoutOrNull                                            | 设置指定的超时在协程内运行给定的代码块，如果超时，返回null   |
| awaitAll                                                     | https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/await-all.html |
| joinAll                                                      | https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/join-all.html |





## Flow：响应式流



Flow 是 Kotlin 协程提供的**冷流**（Cold Stream），用于处理异步数据流，完美替代 RxJava

StateFlow 是**热流**（Hot Stream），表示一个状态，有初始值，适合车机的 UI 状态管理，可替代 LiveData

























