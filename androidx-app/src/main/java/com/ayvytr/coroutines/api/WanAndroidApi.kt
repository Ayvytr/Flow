package com.ayvytr.coroutines.api

import com.ayvytr.coroutines.bean.WanAndroidHome
import retrofit2.http.GET

/**
 * @author Administrator
 */
interface WanAndroidApi {
    @GET("article/list/0/json")
    suspend fun getHomeArticle(): WanAndroidHome
}