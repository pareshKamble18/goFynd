package com.paresh.gofynd.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.paresh.gofynd.model.Article
import com.paresh.gofynd.utils.Constants


@Dao
interface ArticleDao {
    @Insert
    fun insertArticleList(articleDbList: List<Article?>?)

    @Query("SELECT * FROM " + Constants.TABLE_NAME)
    fun fetchAllData(): LiveData<List<Article>>

    @Query("DELETE FROM " + Constants.TABLE_NAME)
    fun deleteAllData()
}