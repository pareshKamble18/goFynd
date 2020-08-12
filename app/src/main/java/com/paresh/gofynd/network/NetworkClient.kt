package com.paresh.gofynd.network

import com.paresh.gofynd.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class NetworkClient {
    private var retrofit: Retrofit? = null


    constructor() {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: NetworkInterface
        get() = retrofit!!.create<NetworkInterface>(NetworkInterface::class.java)

    companion object {
        private var mInstance: NetworkClient? = null

        @get:Synchronized
        val instance: NetworkClient?
            get() {
                if (mInstance == null) {
                    mInstance = NetworkClient()
                }
                return mInstance
            }
    }
}