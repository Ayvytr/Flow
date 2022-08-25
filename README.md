[![Maven Central](https://img.shields.io/maven-central/v/io.github.ayvytr/flow.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ayvytr%22%20AND%20a:%22flow%22)



[![Maven Central](https://img.shields.io/maven-central/v/io.github.ayvytr/flow-list.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ayvytr%22%20AND%20a:%22flow-list%22)



## Import

``` groovy
implementation 'io.github.ayvytr:flow:0.0.6'


//可选：支持下拉刷新和上拉加载的BaseListActivity, BaseListFragment

implementation 'io.github.ayvytr:flow-list:0.0.6'
```







## ChangeLog

### 0.0.6

* BaseFragment增加懒加载功能

### 0.0.5

* 修改gradle-maven-publish-plugin:0.20.0上传的包不是release的问题

### 0.0.4

* 增加**BaseConfig.PAGE_SIZE**为全局单页条目数
* 增加**base-list**模块，**BaseListActivity**，**BaseListFragment**方便下拉刷新和上拉加载

### 0.0.3

* 修改BaseViewModel.view 为protected

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



### flow-list使用参考

```kotlin
class WanAndroidAdapter(context: Context):
    EmptyAdapter<WanAndroidData>(context, R.layout.item_wanandroid, R.layout.layout_empty) {
    override fun onBindView(
        holder: ViewHolder,
        t: WanAndroidData,
        position: Int,
        payloads: List<Any>
    ) {
        holder.setText(R.id.tv_title, t.title)
        holder.setText(R.id.tv_content, t.niceDate)
    }

    override fun onBindEmptyView(holder: ViewHolder) {
    }
}

class WanAndroidListActivity: BaseListActivity<WanAndroidViewModel, WanAndroidData>(),
    WanAndroidView {
    val wanAndroidAdapter = WanAndroidAdapter(getContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
    }


    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        FIRST_PAGE = 0

        setAdapter(wanAndroidAdapter)
        recyclerView.addItemDecoration(DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL))
        wanAndroidAdapter.onItemClickListener = object: MultiItemTypeAdapter.OnItemClickListener<WanAndroidData>{
            override fun onItemClick(
                holder: RecyclerView.ViewHolder,
                t: WanAndroidData,
                position: Int
            ) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(t.link)))
            }
        }


        autoRefresh()
    }

    override fun onRefreshCallback(refreshLayout: RefreshLayout) {
        viewModel.getWanAndroidHome(FIRST_PAGE)
    }

    override fun onLoadMoreCallback(refreshLayout: RefreshLayout) {
        viewModel.getWanAndroidHome(currentPage + 1)
    }

    override fun showWanAndroidHome(wanAndroidHome: WanAndroidHome) {
        updateData(
            wanAndroidHome.data.datas,
            wanAndroidHome.data.curPage,
            wanAndroidHome.data.pageCount
        )
    }

    override fun onWanAndroidHomeFailed() {
        finishRefresh()
    }
}
```



### 懒加载Fragment

```kotlin
/**
 * 是否开启懒加载，懒加载开启时，只改变了[initData]初始化时机. 改为在[onResume]时初始化.
 *    
 * 是否开启懒加载. 注意：在ViewPager中使用时，必须调用FragmentPagerAdapters(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
 * 指明只在onResume时调用initData()数据加载.
 *
 * 注意：[onResume]中调用[initData]时，savedInstanceState=null
*/
class LazyFragment2: BaseFragment<WanAndroidViewModel>(), WanAndroidView {
    override fun getContentViewRes(): Int {
        return R.layout.fragment_lazy
    }

    override fun isLazyLoadEnabled(): Boolean {
        return true
    }
}
```
