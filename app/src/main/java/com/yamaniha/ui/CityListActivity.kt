package com.yamaniha.ui


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yamaniha.R
import com.yamaniha.adapter.CityListAdapter
import com.yamaniha.dto.WeatherDTO
import com.yamaniha.mapper.Mapper
import com.yamaniha.model.City
import com.yamaniha.service.WeatherService
import kotlinx.android.synthetic.main.activity_location_list.*
import java.lang.Exception


class CityListActivity() : AppCompatActivity() {


    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val REQUEST_CODE=1000
    var latitude =0.0
    var longitude =0.0
    var scaleTemperatureDescription = "Celsius"

    var cities =ArrayList<City>()

    var menu : Menu?=null



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menu = menu
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        menu?.let {
            var item = it.findItem(R.id.item_menu_Scale) as MenuItem
            item?.title = "º${Mapper.mapScaleDescriptionToInitial(scaleTemperatureDescription)}"
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.item_menu_Celsius, R.id.item_menu_Fahrenhiet,R.id.item_menu_Kelvin   -> {
            bindViews(item.title.toString(), this.latitude, this.longitude)
            true
        }
        R.id.imMap->{
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("info",WeatherDTO(scaleTemperatureDescription,latitude, longitude, cities))
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)
        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        intent?.extras?.let{

            val info = it.get("info") as WeatherDTO

            cities =info?.cities
            scaleTemperatureDescription = info?.scaleTemperatureDescription

            latitude = info?.latitude
            longitude = info?.longitude
        }



        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE)
        }
        else {

            buildLocationRequest()
            buildLocationCallBack()
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper())
        }

    }

    private fun buildLocationCallBack() {
        locationCallback=object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                var location = p0!!.locations.get(p0!!.locations.size-1)
                latitude = location.latitude
                longitude = location.longitude
                bindViews(scaleTemperatureDescription,latitude, longitude)
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement=10f
    }




    private fun bindViews(temperatureScaleDescription: String, latitude:Double, longitude:Double){

        try {
            val dto = WeatherService.getCitiesAround(
                "pt_Br",
                temperatureScaleDescription,
                latitude,
                longitude
            )

            this.menu?.let {
                var item = it.findItem(R.id.item_menu_Scale) as MenuItem
                item?.title = "º${dto.scaleTemperatureDescription}"
            }

            this.cities = dto.cities
            this.scaleTemperatureDescription = temperatureScaleDescription

            val recyclerView = location_list_recyclerview
            recyclerView.adapter = CityListAdapter(scaleTemperatureDescription,dto.cities, this)
            val layoutManager = StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL
            )
            recyclerView.layoutManager = layoutManager
        } catch (e:Exception){
            Log.e("bindViews", e.message)
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            finish()
        }
    }


}


