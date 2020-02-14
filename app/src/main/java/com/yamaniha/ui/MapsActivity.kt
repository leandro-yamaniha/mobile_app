package com.yamaniha.ui

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yamaniha.R
import com.yamaniha.dto.WeatherDTO
import com.yamaniha.mapper.Mapper
import com.yamaniha.model.City
import com.yamaniha.service.WeatherService
import java.lang.Exception
import kotlin.math.roundToInt


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val REQUEST_CODE=1000

    private lateinit var mMap: GoogleMap
    private lateinit var cities:ArrayList<City>

    private var latitude :Double =0.0
    private var longitude:Double =0.0

    var scaleTemperatureDescription = ""


    var menu : Menu?=null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menu = menu
        menuInflater.inflate(R.menu.toolbar_menu_map,menu)
        menu?.let {
            var item = it.findItem(R.id.item_menu_Scale_Map) as MenuItem
            item?.title="º${Mapper.mapScaleDescriptionToInitial(this.scaleTemperatureDescription)}"
        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.item_menu_Celsius_Map, R.id.item_menu_Fahrenhiet_Map,R.id.item_menu_Kelvin_Map   -> {
            bindViews(item.title.toString(), this.latitude, this.longitude)
            true
        }
        R.id.imList -> {

            val intent = Intent(this, CityListActivity::class.java)
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
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
                Looper.myLooper())
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        includeMarkers()


    }

    private fun includeMarkers() {

        menu?.let {
            var item = it.findItem(R.id.item_menu_Scale_Map) as MenuItem
            item?.title="º${Mapper.mapScaleDescriptionToInitial(this.scaleTemperatureDescription)}"
        }

        mMap.clear()
        for (city in cities) {
            val coordCity = LatLng(city.latitude, city.longitude)

            mMap.addMarker(MarkerOptions().position(coordCity).title("${city.name}").snippet("${city.temperature.roundToInt()}º ${city.scale}"))
        }

        val coord = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 10.00.toFloat()))

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

            this.cities = dto.cities
            this.scaleTemperatureDescription = temperatureScaleDescription

            includeMarkers()
        } catch (e:Exception){
            Log.e("bindViews", e.message)
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            finish()
        }

    }


}
