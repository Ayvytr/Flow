package com.ayvytr.coroutines.api

import com.ayvytr.coroutines.bean.WanAndroidHome
import com.ayvytr.coroutines.bean.WanAndroidHotKey
import retrofit2.http.GET

/**
 * @author Administrator
 */
interface WanAndroidApi {
    @GET("article/list/0/json")
    suspend fun getHomeArticle(): WanAndroidHome

    @GET("hotkey/json")
    suspend fun getHotKey(): WanAndroidHotKey
}