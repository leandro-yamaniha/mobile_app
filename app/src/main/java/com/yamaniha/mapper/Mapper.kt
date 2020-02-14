package com.yamaniha.mapper

import com.yamaniha.dto.WeatherResponseBodyDTO
import com.yamaniha.model.City

class Mapper {

    companion object {
        fun mapBodyToCities(body: WeatherResponseBodyDTO, scale: String): ArrayList<City> {
            val list = ArrayList(
                body.list
                    .map { item ->
                        City(
                            item.name,
                            item.coord.lat,
                            item.coord.lon,
                            item.weather.first().icon,
                            item.weather.first().description,
                            item.main.temp,
                            item.main.temp_min,
                            item.main.temp_max,
                            scale
                        )
                    })
            return list
        }

        fun mapScaleDescriptionToInitial(description:String):String{
            if (description.equals("Celsius")) {
                return "C"
            } else if (description.equals("Fahrenheit")) {
                return "F"
            } else {
                return "K"
            }
        }

        fun mapScaleToUnit(initial:String):String{
            if (initial.equals("C")) {
                return "metric"

            } else if (initial.equals("F")) {
                return "imperial"
            } else {
                return ""
            }
        }

        fun getUrlIcon(iconId:String)= "http://openweathermap.org/img/w/$iconId.png".format(this)
    }
}