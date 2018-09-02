package nl.booxchange.utilities

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vcristian.combus.dismiss

open class BaseViewModel: ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val hasFailed = MutableLiveData<Boolean>()

    fun onLoadingStarted() {
        isLoading.postValue(true)
        hasFailed.postValue(false)
    }

    fun onLoadingFailed() {
        hasFailed.postValue(true)
    }

    fun onLoadingFinished() {
        isLoading.postValue(false)
    }

    override fun onCleared() {
        dismiss()
    }
}
