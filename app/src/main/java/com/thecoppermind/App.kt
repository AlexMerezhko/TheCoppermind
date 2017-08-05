package com.thecoppermind

import android.app.Application
import com.thecoppermind.network.RestApi
import com.thecoppermind.network.TheCoppermindApi

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val restApi : RestApi = RestApi()
        mainApi = restApi.mainApi
    }

    companion object {
        lateinit var mainApi : TheCoppermindApi
    }
}