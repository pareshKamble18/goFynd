package com.paresh.gofynd.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paresh.gofynd.model.Article


@TypeConverters(Converters_Source::class)
@Database(entities = [Article::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}