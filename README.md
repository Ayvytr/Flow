[![Maven Central](https://img.shields.io/maven-central/v/io.github.ayvytr/flow.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ayvytr%22%20AND%20a:%22flow%22)



## Import

``` groovy
implementation 'io.github.ayvytr:flow:0.0.1'
```



## 说明

### BaseConfig：

1. 可全局自定义网络错误字符串，方便引用string.xml字符串
2. 全局设置请求失败重试次数
3. 全局showLoading(boolean)
4. 全局自定义网络错误提示

### launchFlow()：

1. 进行了封装，可决定接口调用时是否showLoading, 是否重试，是否可以repeatSameJob（是否允许同一接口同时多次请求）
2. 只提供了onSuccess, onError的回调，且onError回调有默认处理。未提供onLoading, onComplete回调



## 使用

### 1.Application中自定义BaseConfig，自定义网络错误字符串，请求失败重试次数，全局showLoading(boolean)，全局网络错误提示

```kotlin
override fun onCreate() {
super.onCreate()
//...网络请求等初始化

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
            }
            else                      -> {
                NetworkException(e, R.string.other_error)
            }
        }

        exception
    }
    //showLoading：显示Dialog
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
    //showMessage:显示Snackbar
    BaseConfig.onShowMessage = {context, message, rootView ->
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
```

### 2.分别继承BaseActivity/BaseFragment，BaseViewModel，在ViewModel中调用launchFlow()进行网络请求

```kotlin
class MainActivity : BaseActivity<MainViewModel>() {
	//其他初始化
   override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
    }

   override fun initData(savedInstanceState: Bundle?) {
        btn_get_data.setOnClickListener {
            //基本测试
            viewModel.getAndroidPostFlow(
                {
                    //onSuccess
                    tv_value.text = it.toString()
                },{
                    //onError：会覆盖全局的showMessage。如果不要覆盖，请使用ErrorObserver处理onError
                })
        }
    }
}

class MainViewModel : BaseViewModel() {
	val gankApi = ApiClient.create(GankApi::class.java)
	//网络请求

   fun getAndroidPostFlow(
        success: (BaseGank) -> Unit,
        error: ((NetworkException) -> Unit)? = null
    ) {
        launchFlow({ gankApi.getAndroidGankSuspend() }, success, true, false, true, error)
    }

}
```





## ChangeLog

* 0.0.1 第一版





