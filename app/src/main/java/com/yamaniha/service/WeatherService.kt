package com.yamaniha.service

import android.util.Log
import com.yamaniha.client.WeatherApiInitializer
import com.yamaniha.dto.WeatherDTO
import com.yamaniha.mapper.Mapper
import com.yamaniha.model.City

class WeatherService {


    companion object {

        private const val apiKey="df7fabbfda8b954b457dbb4d5b205fc5"

        private var oldLatitude: Double = 0.0
        private var oldLongitude: Double = 0.0
        private var oldTemperatureScaleDescription: String = ""
        private var  oldWeather: WeatherDTO= WeatherDTO("",0.0,0.0, ArrayList<City>())

        fun getCitiesAround(
            language: String,
            temperatureScaleDescription: String,
            latitude: Double,
            longitude: Double
        ): WeatherDTO {

            Log.d("getCitiesAround","language: ${language} scale: ${temperatureScaleDescription} latitude: ${latitude} longitude: ${longitude}")

            var initial = Mapper.mapScaleDescriptionToInitial(temperatureScaleDescription)
            if (!temperatureScaleDescription.equals(this.oldTemperatureScaleDescription) ||
                    !latitude.equals(oldLatitude) ||
                    !longitude.equals(oldLongitude)) {

                Log.d("getCitiesAround","Updating data weather ....")
                var units = Mapper.mapScaleToUnit(initial)
                val call = WeatherApiInitializer().weatherServiceApi().getCitiesInCycle(
                    latitude,
                    longitude,
                    50,
                    units,
                    language,
                    apiKey
                )

                var response = call.execute()
                response?.body()?.let {
                    val cities = Mapper.mapBodyToCities(it, initial)

                    oldLatitude = latitude
                    oldLongitude= longitude
                    oldTemperatureScaleDescription = temperatureScaleDescription
                    oldWeather = WeatherDTO(initial, latitude, longitude, cities)

                    return oldWeather
                }
            }
            else {
                Log.d("getCitiesAround","No changes using cache ....")
                return oldWeather
            }

            return WeatherDTO(initial,latitude, longitude, ArrayList<City>())
        }
    }



}