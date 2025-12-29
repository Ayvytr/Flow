package com.ayvytr.flow

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ayvytr.flow.base.IView
import com.ayvytr.flow.internal.BaseLifecycleObserver
import com.ayvytr.flow.internal.getVmClass
import com.ayvytr.flow.vm.BaseViewModel

/**
 * [BaseActivity].
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.1.0 修改[initViewModel]，保证[viewModel]初始化成功（解决泛型信息缺失导致的[getVmClass]异常问题）
 * @since 0.0.1
 */
open class BaseActivity<T: BaseViewModel<IView>>: AppCompatActivity(), IView {

    /**
     * 写成lateinit，在onCreate初始化的原因：1.传savedInstanceState；2.在继承[BaseActivity]时，可以把
     * [setContentView]写在[onCreate]里，不然不写在[initView]里时需要再加一个获取布局的方法
     */
    private lateinit var baseObserver: BaseLifecycleObserver

    protected lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initBaseObserver(savedInstanceState)
    }

    private fun initBaseObserver(savedInstanceState: Bundle?) {
        baseObserver = object:
            BaseLifecycleObserver {
            override fun onCreateEvent() {
                initView(savedInstanceState)
                initData(savedInstanceState)
            }
        }
        lifecycle.addObserver(baseObserver)
    }

    /**
     * 初始化[viewModel]
     * @since 0.1.0 解决泛型信息缺失导致的[getVmClass]异常问题
     */
    open fun initViewModel() {
        viewModel = try {
            ViewModelProvider(this)[getViewModelClass()]
        } catch (e: Exception) {
            ViewModelProvider(this)[BaseViewModel::class.java] as T
        }
        viewModel.setIView(this)
    }

    /**
     * 如果继承的子类传入的泛型不是[BaseViewModel],需要重写这个方法，提供自定义的[BaseViewModel]子类.
     *
     * 注意：[getVmClass]报如下错时需要重写这个方法显式指明[T]的类型（这个情况是继承多层后没有获取到
     * 泛型信息导致的）：
     * ClassCastException: java.lang.Class cannot be cast to java.lang.reflect.ParameterizedType
     */
    private fun getViewModelClass(): Class<T> {
        return getVmClass(this) as Class<T>
    }

    /**
     * 没有提供getLayoutRes方法获取子类布局，可以在[onCreate]，或者[initView]中调用[setContentView]
     * 初始化view.
     */
    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun onStop() {
        super.onStop()
        showLoading(false)
    }

    override fun onDestroy() {
        lifecycle.removeObserver(baseObserver)
        super.onDestroy()
    }

    override fun showLoading(isShow: Boolean) {
        BaseConfig.onShowLoading(this, isShow)
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
        BaseConfig.onShowMessage(this, message, findViewById(android.R.id.content))
    }

}

