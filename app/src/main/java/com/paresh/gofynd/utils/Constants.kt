package com.paresh.gofynd.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager


class Constants(var context: Context) {
    val isOnline: Boolean
        get() {
            val activeNetworkInfo =
                (context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            return activeNetworkInfo != null &&
                    activeNetworkInfo.isConnectedOrConnecting
        }

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
        const val IMAGE_URL = "IMAGE_URL"
        const val LANGUAGE = "en"
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 1
        const val SITE_NAME = "google"
        const val API_KEY = "3145d60eaf18468ea2a92b6875f9cb51"

        // DB :
        const val DB_NAME = "articles_db"
        const val TABLE_NAME = "article"
    }

}