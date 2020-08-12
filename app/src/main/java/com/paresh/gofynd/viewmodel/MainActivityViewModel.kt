package com.paresh.gofynd.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.paresh.gofynd.model.Article
import com.paresh.gofynd.model.MainModel
import com.paresh.gofynd.repository.MainActivityRepo


public class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLiveDataMainModel: MutableLiveData<MainModel>
    private var mutableLiveDataLoadMoreMainModel: MutableLiveData<MainModel>? = null
    private var mutableLiveDataGetDbData: LiveData<List<Article>>? = null
    var mainActivityRepo: MainActivityRepo
    fun initLoadMore(current: Int) {
        mutableLiveDataLoadMoreMainModel = mainActivityRepo.getLoadMoreData(current)
    }

    val data: LiveData<MainModel>
        get() = mutableLiveDataMainModel

    val loadMoreData: LiveData<MainModel>?
        get() = mutableLiveDataLoadMoreMainModel

    // Offline :
    val dbData: LiveData<List<Article>>?
        get() = mutableLiveDataGetDbData

    fun initDeleteDbData() {
        mainActivityRepo.deleteAllData()
    }

    fun initGetDbData() {
        mutableLiveDataGetDbData = mainActivityRepo.dbArticleData
    }

    fun insert(articleList: List<Article?>) {
        mainActivityRepo.insertList(articleList)
    }

    init {
        mainActivityRepo = MainActivityRepo.getInstance(application)
        mutableLiveDataMainModel = mainActivityRepo.data
    }
}
