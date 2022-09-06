package com.ayvytr.flow

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ayvytr.flow.base.IView
import com.ayvytr.flow.internal.getVmClass
import com.ayvytr.flow.vm.BaseViewModel

/**
 * [BaseFragment].
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.6 增加懒加载功能
 * @since 0.0.1
 */
abstract class BaseFragment<T: BaseViewModel<IView>>: Fragment(), IView {

    protected lateinit var viewModel: T

    /**
     * 是否开启懒加载，懒加载开启时，只改变了[initData]初始化时机. 改为在[onResume]时初始化.
     *
     * 注意：在ViewPager中使用时，必须调用FragmentPagerAdapters(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
     * 指明只在onResume时调用initData()数据加载.
     *
     * 注意：[onResume]中调用[initData]时，savedInstanceState=null
     */
    protected var isLazyLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            inflater.inflate(getContentViewRes(), container, false)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    @LayoutRes
    abstract fun getContentViewRes(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        if (!isLazyLoadEnabled()) {
            initData(savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        //增加了Fragment是否可见的判断
        if (isLazyLoadEnabled()) {
            if (!isLazyLoaded && !isHidden) {
                initData(null)
                isLazyLoaded = true
            }
        }
    }

    /**
     * 如果继承的子类传入的泛型不是[BaseViewModel],需要重写这个方法，提供自定义的[BaseViewModel]子类.
     *
     * 注意：[getVmClass]报如下错时需要重写这个方法显式指明[T]的类型（这个情况是继承多层Fragment后没有获取到
     * 泛型导致的）：
     * ClassCastException: java.lang.Class cannot be cast to java.lang.reflect.ParameterizedType
     */
    protected open fun getViewModelClass(): Class<T> {
        return getVmClass(this) as Class<T>
    }

    open fun initViewModel() {
        viewModel = ViewModelProvider(this)[getViewModelClass()]
        viewModel.setIView(this)
    }


    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
    }


    override fun showLoading(isShow: Boolean) {
        BaseConfig.onShowLoading(requireContext(), isShow)
    }

    /**
     * @see showLoading
     */
    override fun hideLoading() {
        showLoading(false)
    }

    override fun showMessage(@StringRes strId: Int) {
        showMessage(getString(strId))
    }

    override fun showMessage(message: CharSequence) {
        BaseConfig.onShowMessage(requireContext(), message, requireView())
    }

    /**
     * 是否开启懒加载.
     *
     * 注意：在ViewPager中使用时，必须调用FragmentPagerAdapters(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
     * 指明只在onResume时调用initData()数据加载
     * @since 0.0.6
     */
    protected open fun isLazyLoadEnabled(): Boolean {
        return false
    }
}

