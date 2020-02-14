package com.yamaniha.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    val name:String,
    val latitude: Double,
    val longitude:Double,
    val weatherIcon: String,
    val weatherDescription: String,
    val temperature: Double,
    val min: Double,
    val max:Double,
    val scale: String ): Parcelable