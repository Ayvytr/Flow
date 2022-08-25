package com.ayvytr.flowapp.api

import com.ayvytr.flowapp.bean.BaseGank
import retrofit2.http.GET

/**
 * @author EDZ
 */
interface GankApi {
    @GET("data/Android/2/1")
    suspend fun getAndroidGankSuspend(): BaseGank

    @GET("data/IOS/2/1")
    suspend fun getIosGankSuspend(): BaseGank

}
