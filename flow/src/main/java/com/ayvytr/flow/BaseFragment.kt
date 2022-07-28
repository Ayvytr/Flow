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
 * @since 0.0.1
 */
abstract class BaseFragment<T: BaseViewModel<IView>>: Fragment(), IView {

    protected lateinit var viewModel: T

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
        initData(savedInstanceState)
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
        viewModel.view = this
    }


    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
    }


    override fun showLoading(isShow: Boolean) {
        BaseConfig.onShowLoading(requireContext(), isShow)
    }

    override fun showMessage(@StringRes strId: Int) {
        showMessage(getString(strId))
    }

    override fun showMessage(message: CharSequence) {
        BaseConfig.onShowMessage(requireContext(), message, requireView())
    }

}

