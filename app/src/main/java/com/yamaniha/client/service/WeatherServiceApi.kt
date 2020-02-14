package com.yamaniha.client.service

import com.yamaniha.dto.WeatherResponseBodyDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceApi {

    @GET("data/2.5/find")
    fun getCitiesInCycle(
            @Query("lat") lat:Double,
             @Query("lon") lon:Double,
             @Query("cnt") cnt:Int,
             @Query("units") units:String,
             @Query("lang") lang:String,
             @Query("APPID") appId:String
            ): Call<WeatherResponseBodyDTO>
}