package com.ayvytr.flowapp.api

import com.ayvytr.flowapp.bean.WanAndroidHome
import com.ayvytr.flowapp.bean.WanAndroidHotKey
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