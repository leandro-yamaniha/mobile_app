package com.yamaniha.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yamaniha.R
import com.yamaniha.dto.WeatherDTO
import com.yamaniha.mapper.Mapper
import com.yamaniha.model.City
import com.yamaniha.ui.MapsActivity
import kotlinx.android.synthetic.main.location_weather_item.view.*
import kotlin.math.roundToInt


class CityListAdapter(private val scaleTemperatureDescription:String, private val cities:ArrayList<City>, private val context:Context) : RecyclerView.Adapter<CityListAdapter.ViewHolder>(){

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.location_weather_item, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cities[position]
        holder.let {

            Picasso.get().load(Mapper.getUrlIcon(city.weatherIcon )).into(it.icon)
            Log.d("CityListAdapter",Mapper.getUrlIcon(city.weatherIcon ))

            it.city.text = city.name
            it.situation.text = city.weatherDescription
            it.curTemp.text = "${city.temperature.roundToInt()}º ${city.scale}"
            it.minTemp.text = "Min.${city.min.roundToInt()}º ${city.scale}"
            it.maxTemp.text = "Max. ${city.max.roundToInt()}º ${city.scale}"

            it.card_view.setOnClickListener(View.OnClickListener {

                val intent = Intent(context, MapsActivity::class.java)
                intent.putExtra("info",
                    WeatherDTO(scaleTemperatureDescription,city.latitude, city.longitude, cities)
                )
                context.startActivity(intent)

            })

        }


    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val city = itemView.tvCity
        val icon = itemView.ivSituation
        val situation = itemView.tvSituation
        val curTemp = itemView.tvCurTemperature
        val minTemp = itemView.tvMinTemperature
        val maxTemp = itemView.tvMaxTemperature
        val card_view = itemView.cvCity


        fun bindView(city : City) {

            Log.d("url", Mapper.getUrlIcon(city.weatherIcon ))
            Picasso.get().load(Mapper.getUrlIcon(city.weatherIcon )).into(icon)
            this.city.text = city.name
            situation.text = city.weatherDescription
            curTemp.text = city.temperature.toString()
            minTemp.text = city.min.toString()
            maxTemp.text = city.max.toString()

        }



        fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
            itemView.setOnClickListener {
                event.invoke(getAdapterPosition(), getItemViewType())
            }
            return this
        }

    }





}

