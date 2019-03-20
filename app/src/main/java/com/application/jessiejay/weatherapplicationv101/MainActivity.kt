package com.application.jessiejay.weatherapplicationv101

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), LocationListener {
    companion object {
        val baseUrl = "http://api.openweathermap.org/"
        val appid = "319af09ba07aabb7b53de35862c73840"
        val TAG = "MyActivity"
        val CELSIUS_UNIT_API = "metric"
        val CELSIUS_UNIT_TEXT = "°C"
        val FAHRENHEIT_UNIT_API = "imperial"
        val FAHRENHEIT_UNIT_TEXT = "°F"
    }

    //default values : seattle, Fahrenheit
    private var lat = "47.618503"
    private var lon = "-122.315176"
    private var unit = FAHRENHEIT_UNIT_API

    private lateinit var viewModel: WeatherViewModel
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var locationManager: LocationManager
//    private lateinit var locationTextView: TextView
    private lateinit var currentCityName: TextView
    private lateinit var currentDateTime: TextView
    private lateinit var currentTemp: TextView
    private lateinit var tempUnitText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        locationTextView = findViewById(R.id.current_location)
        currentCityName = findViewById(R.id.city_name_textview)
        currentDateTime = findViewById(R.id.current_time_textview)
        currentTemp = findViewById(R.id.temp_textview)
        tempUnitText = findViewById(R.id.temp_unit_textview)

        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        val database = DatabaseHelper(this)
        firebaseAuth = FirebaseAuth.getInstance()
        val permissionCheck = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 700)
        }
        getLocation()
        getCurrentData()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            when(menuItem.itemId){
                R.id.drawer_logout ->{
                    firebaseAuth.signOut()
                    finish()
                }
                R.id.drawer_settings ->{

                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getLocation() {
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5.toFloat(), this)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }

    fun getCurrentData() {
        val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
        val service: WeatherService = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeatherInfo(lat,lon,appid,unit)
        //add spinner
        call.enqueue(object: Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>?, response: Response<WeatherResponse>?) {
                if(response!!.code()==200){
                    val weatherResponse = response.body()
                    viewModel.setValues(weatherResponse)
                    viewModel.setCurrentTime()
                    //stop spinner
                    displayWeather()
                }
            }
            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {

            }
        })
    }

    fun displayWeather() {
        currentCityName.text = viewModel.cityName
        currentDateTime.text = viewModel.currentTime_str
        currentTemp.text = viewModel.temp.toString()
        if(unit.equals(FAHRENHEIT_UNIT_API)){
            tempUnitText.text = FAHRENHEIT_UNIT_TEXT
        } else {
            tempUnitText.text = CELSIUS_UNIT_TEXT
        }

    }
    override fun onLocationChanged(location: Location?) {
//        locationTextView.setText("Current Location: " + location?.latitude + ", " + location?.longitude)
        lat = location?.latitude.toString()
        lon = location?.longitude.toString()
        getCurrentData()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Toast.makeText(applicationContext, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show()
    }


}
