package com.ayvytr.coroutines.api

import com.ayvytr.coroutines.bean.WanAndroidHome
import com.ayvytr.coroutines.bean.WanAndroidHotKey
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author Administrator
 */
interface WanAndroidApi {
    @GET("article/list/{page}/json")
    suspend fun getHomeArticle(@Path("page") page: Int): WanAndroidHome

    @GET("hotkey/json")
    suspend fun getHotKey(): WanAndroidHotKey
}