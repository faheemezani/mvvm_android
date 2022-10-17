package dev.mfaheemezani.mvvm.helpers.network

import dev.mfaheemezani.mvvm.data.network.response.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewYorkTimesAPI {

    // Top Stories (Home Section) API
    @GET("/svc/topstories/v2/home.json")
    suspend fun getHomeTopStories(@Query("api-key") apiKey: String): Response

}