package com.paresh.gofynd.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.paresh.gofynd.db.ArticleDao
import com.paresh.gofynd.db.MyDatabase
import com.paresh.gofynd.model.Article
import com.paresh.gofynd.model.MainModel
import com.paresh.gofynd.network.NetworkClient
import com.paresh.gofynd.network.NetworkInterface


import com.paresh.gofynd.utils.Constants
import com.paresh.gofynd.utils.Constants.Companion.API_KEY
import com.paresh.gofynd.utils.Constants.Companion.FIRST_PAGE
import com.paresh.gofynd.utils.Constants.Companion.LANGUAGE
import com.paresh.gofynd.utils.Constants.Companion.PAGE_SIZE
import com.paresh.gofynd.utils.Constants.Companion.SITE_NAME
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivityRepo(context: Context?) {
    var myDatabase: MyDatabase
    private val mArticleDao: ArticleDao
    val dbArticleData: LiveData<List<Article>>
    val networkInterface: NetworkInterface
    val data: MutableLiveData<MainModel>
        get() {
            val newData = MutableLiveData<MainModel>()
            networkInterface.getData(API_KEY, SITE_NAME, LANGUAGE, PAGE_SIZE, FIRST_PAGE)
                .enqueue(object : Callback<MainModel?> {
                    override fun onResponse(
                        call: Call<MainModel?>,
                        response: Response<MainModel?>
                    ) {
                        if (response.isSuccessful) {
                            newData.setValue(response.body())
                        }
                    }

                    override fun onFailure(
                        call: Call<MainModel?>,
                        t: Throwable
                    ) {
                        newData.setValue(null)
                    }
                })
            return newData
        }

    fun getLoadMoreData(currentPage: Int): MutableLiveData<MainModel> {
        val newData = MutableLiveData<MainModel>()
        networkInterface.getData(API_KEY, SITE_NAME, LANGUAGE, PAGE_SIZE, currentPage)
            .enqueue(object : Callback<MainModel?> {
                override fun onResponse(
                    call: Call<MainModel?>,
                    response: Response<MainModel?>
                ) {
                    if (response.isSuccessful) {
                        newData.setValue(response.body())
                    }
                }

                override fun onFailure(
                    call: Call<MainModel?>,
                    t: Throwable
                ) {
                    newData.setValue(null)
                }
            })
        return newData
    }

    fun insertList(articleList: List<Article?>) {
        insertAsyncTask(mArticleDao).execute(articleList)
    }

    private class insertAsyncTask internal constructor(dao: ArticleDao) :
        AsyncTask<List<Article?>?, Void?, Void?>() {
        private val mAsyncTaskDao: ArticleDao
         override fun doInBackground(vararg params: List<Article?>?): Void? {
            mAsyncTaskDao.insertArticleList(params[0])
            return null
        }

        init {
            mAsyncTaskDao = dao
        }
    }

    @SuppressLint("StaticFieldLeak")
    fun deleteAllData() {
        object : AsyncTask<String?, String?, String?>() {
            override protected fun doInBackground(vararg params: String?): String? {
                Log.e("Hiiiiii", "deleteAllData")
                myDatabase.articleDao()!!.deleteAllData()
                return null
            }

            override fun onPostExecute(todoList: String?) {}

        }.execute()
    }
    companion object {
        lateinit var mainActivityRepo: MainActivityRepo
        fun getInstance(context: Context?): MainActivityRepo {

                mainActivityRepo = MainActivityRepo(context)

            return mainActivityRepo
        }
    }

    init {

        networkInterface = NetworkClient.instance!!.api
        myDatabase =
            Room.databaseBuilder<MyDatabase>(context!!, MyDatabase::class.java, Constants.DB_NAME)
                .fallbackToDestructiveMigration().build()
        mArticleDao = myDatabase.articleDao()
        dbArticleData = mArticleDao.fetchAllData()
    }
}