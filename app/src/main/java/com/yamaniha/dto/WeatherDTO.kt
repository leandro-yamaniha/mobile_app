package com.yamaniha.dto

import android.os.Parcelable
import com.yamaniha.model.City
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherDTO (
    val scaleTemperatureDescription:String,
    val latitude: Double,
    val longitude: Double,
    val cities:ArrayList<City>) : Parcelable