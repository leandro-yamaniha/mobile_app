package com.yamaniha.client

import com.yamaniha.client.service.WeatherServiceApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiInitializer {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun weatherServiceApi(): WeatherServiceApi = retrofit.create(WeatherServiceApi::class.java)

}