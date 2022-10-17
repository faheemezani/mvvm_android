package dev.mfaheemezani.mvvm.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mfaheemezani.mvvm.data.network.response.Response
import dev.mfaheemezani.mvvm.helpers.Config
import dev.mfaheemezani.mvvm.helpers.network.NewYorkTimesBackendAccess
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewYorkTimesViewModel: ViewModel() {

    val response: MutableLiveData<Response?> = MutableLiveData()
    val errorResponse: MutableLiveData<String> = MutableLiveData()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorResponse.value = (throwable.localizedMessage ?: throwable.message ?: throwable.cause).toString()
        Log.e(this::class.java.simpleName, throwable.toString()) }

    fun getHomeTopStoriesOnline() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            response.postValue(NewYorkTimesBackendAccess.retrofit.getHomeTopStories(Config.NYT_API_KEY))
        }
    }
}