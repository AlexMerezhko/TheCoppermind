package com.thecoppermind.network

import com.google.gson.GsonBuilder
import com.thecoppermind.page.PageData
import com.thecoppermind.page.PageClassDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestApi {

    internal val mainApi: TheCoppermindApi

    init {
        val gson = GsonBuilder()
        gson.registerTypeAdapter(PageData::class.java, PageClassDeserializer());

        val retrofit = Retrofit.Builder()
                .baseUrl("https://coppermind.net")
                .addConverterFactory(GsonConverterFactory.create(gson.create()))
                .build()

        mainApi = retrofit.create(TheCoppermindApi::class.java)
    }
}

