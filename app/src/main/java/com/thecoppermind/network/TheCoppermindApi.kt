package com.thecoppermind.network

import com.thecoppermind.page.PageData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TheCoppermindApi {


//    @GET("https://coppermind.net/w/api.php?action=query&titles=Vin&prop=revisions&rvprop=content&format=json")
//    @GET("/w/api.php?action=query&titles=Vin&prop=revisions&rvprop=content&format=json")

//    @GET("https://coppermind.net/w/api.php?action=parse&format=json")


    @GET("/w/api.php?action=parse&format=json")
    fun requestPageByIdXml(@Query("pageid") pageId: Int): Call<PageData>

//    @GET("/w/api.php?action=parse&format=json&prop=wikitext")
//    fun requestPageById(@Query("pageid") pageId: Int): Call<Page>

    @GET("/w/api.php?action=parse&format=json&prop=wikitext")
    fun requestPageById(@Query("page") pageId: String): Call<PageData>
}
