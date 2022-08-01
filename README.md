[![Maven Central](https://img.shields.io/maven-central/v/io.github.ayvytr/flow.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ayvytr%22%20AND%20a:%22flow%22)



## Import

``` groovy
implementation 'io.github.ayvytr:flow:0.0.1'
```







## ChangeLog

### 0.0.2

* 变更职能：增加[BaseViewModel]泛型[IView]，支持BaseActivity, BaseFragment重写：

  1. 方便接口回调写在BaseViewModel中，Activity,Fragment只做需要的ui回调
  2. 方便回传参数

* 增加**BaseViewModel.zipFlow()** ，可合并2个请求

* 修改BaseViewModel：**launchFlow**，**zipFlow()**参数onError顺序

* ~~ErrorObserver~~

  

### 0.0.1 第一版





## 说明

## NetworkException：

* 使用**stringId**显示错误信息：方便做国际化



### BaseConfig：

1. 可全局自定义网络错误提示
2. 全局设置请求失败重试次数
3. 全局showLoading(boolean)



### launchFlow()：

1. 进行了封装，可决定接口调用时是否showLoading, 是否重试，是否可以repeatSameJob（是否允许同一接口同时多次请求）
2. 只提供了onSuccess, onError的回调，且onError回调有默认处理。未提供onLoading, onComplete回调



## 使用

**分别继承BaseActivity/BaseFragment，BaseViewModel，IView**，ui和ViewModel分工协作（需要Repository，DataBinding请自行添加）

```kotlin
interface WanAndroidView: IView {
    fun showWanAndroidHome(wanAndroidHome: WanAndroidHome)
}

//需要泛型WanAndroidViewModel，实现WanAndroidView
class WanAndroidActivity: BaseActivity<WanAndroidViewModel>(), WanAndroidView {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setContentView(R.layout.activity_wan_android)
    }
    override fun initData(savedInstanceState: Bundle?) {
        viewModel.getWanAndroidHome()
    }

    override fun showWanAndroidHome(wanAndroidHome: WanAndroidHome) {
        tv_value.text = wanAndroidHome.toString()
    }
}


//需要传递WanAndroidView
class WanAndroidViewModel: BaseViewModel<WanAndroidView>() {

    val wanAndroidApi = ApiClient.getRetrofit(App.WAN_ANDROID_BASE_URL)
        .create(WanAndroidApi::class.java)

    fun getWanAndroidHome() {
        launchFlow(
            request = { wanAndroidApi.getHomeArticle() },
            onSuccess = { view.showWanAndroidHome(it) },
            onError = { view.showMessage(it.stringId) }
        )
    }
}
```





### Application中可自定义BaseConfig：自定义网络错误提示，请求失败重试次数，全局showLoading(boolean)

**如果BaseConfig.onShowLoading 使用了Dialog，请及时释放**

```kotlin
class App: Application() {
    override fun onCreate() {
        super.onCreate()
			// ...
        initBaseConfig()
    }

    val loadingMap = mutableMapOf<String, LoadingDialog>()

    private fun initBaseConfig() {
        BaseConfig.networkExceptionConverter = { e ->
            var exception = NetworkException(e)
            val networkAvailable = isNetworkAvailable()
            when (e) {
                is SocketTimeoutException -> {
                    if (e.message!!.startsWith("failed to connect to")) {
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
                    e.message?.apply {
                        if(contains("404")) {
                            exception.stringId = R.string.http_404
                        }
                    }
                }
                else                      -> {
                    NetworkException(e, R.string.other_error)
                }
            }

            exception
        }
        BaseConfig.onShowLoading = { context, isShow ->
            val name = context.javaClass.name
            if(isShow) {
                var dialog = loadingMap[name]
                if(dialog == null) {
                    dialog = LoadingDialog(context)
                    loadingMap[name] = dialog
                }
                dialog.show()
            } else {
                loadingMap[name]?.dismiss()
                loadingMap.remove(name)
            }
        }
        BaseConfig.onShowMessage = {context, message, rootView ->
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
        }
    }
}
```

