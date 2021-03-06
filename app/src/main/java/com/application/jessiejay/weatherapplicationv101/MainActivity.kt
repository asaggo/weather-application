package com.application.jessiejay.weatherapplicationv101

import android.Manifest
import android.app.Activity
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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        val REQUEST_CODE_FIND_PLACE = 200
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
    private lateinit var userName: TextView
    private lateinit var weatherImage: ImageView
    private lateinit var currentCityName: TextView
    private lateinit var currentDateTime: TextView
    private lateinit var currentTemp: TextView
    private lateinit var tempUnitText: TextView
    private lateinit var changeUnitBtn: Button
    private lateinit var minTemp: TextView
    private lateinit var humidity: TextView
    private lateinit var cloud: TextView

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var backToCurrentLocation: Button

    private lateinit var coordinates: MutableList<Double>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        locationTextView = findViewById(R.id.current_location)
        userName = findViewById(R.id.user_name_textview)
        weatherImage = findViewById(R.id.weather_image)
        currentCityName = findViewById(R.id.city_name_textview)
        currentDateTime = findViewById(R.id.current_time_textview)
        currentTemp = findViewById(R.id.temp_textview)
        tempUnitText = findViewById(R.id.temp_unit_textview)

        changeUnitBtn = findViewById(R.id.change_unit_button)
        minTemp = findViewById(R.id.temp_min_textview)
        humidity = findViewById(R.id.humidity_textview)
        cloud = findViewById(R.id.cloud_value_textview)
        backToCurrentLocation = findViewById(R.id.back_to_current_location_button)

        changeUnitBtn.setOnClickListener {
            if(changeUnitBtn.text.equals(FAHRENHEIT_UNIT_TEXT)){
                unit = FAHRENHEIT_UNIT_API
                changeUnitBtn.text = CELSIUS_UNIT_TEXT
            } else {
                unit = CELSIUS_UNIT_API
                changeUnitBtn.text = FAHRENHEIT_UNIT_TEXT
            }
            getCurrentData()
        }

        backToCurrentLocation.setOnClickListener{
            getLocation()
            getCurrentData()
        }

        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val databaseReference = firebaseDatabase.getReference(firebaseAuth.uid!!)
        val addValueEventListener = databaseReference.addValueEventListener(object:ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                userName.text = "Welcome, " + user!!.name
            }
        })

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
                    val intent = Intent(applicationContext, loginActivity::class.java)
                    startActivity(intent)
                }
                R.id.drawer_map ->{
                    val intent = Intent(applicationContext, FindPlaceActivity::class.java)
                    startActivityForResult(intent,REQUEST_CODE_FIND_PLACE)
                }
            }
            true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE_FIND_PLACE){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(this,"Selected new location successfully",Toast.LENGTH_SHORT).show()
                lat = data!!.getStringExtra("latitude")
                lon = data!!.getStringExtra("longitude")
                getCurrentData()
            }
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

    private fun getLocation() {
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5.toFloat(), this)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }

    private fun getCurrentData() {
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

    private fun displayWeather() {
        currentCityName.text = viewModel.cityName
        currentDateTime.text = viewModel.currentTime_str
        currentTemp.text = viewModel.temp.toString()
        minTemp.text = viewModel.temp_min.toString()
        humidity.text = viewModel.humidity.toString() + " %"
        cloud.text = viewModel.cloud.toString() + " %"
        if(viewModel.cloud > 50){
            weatherImage.setImageResource(R.drawable.ic_cloud_black_24dp)
        } else {
            weatherImage.setImageResource(R.drawable.ic_wb_sunny_24dp)
        }

        if(unit.equals(FAHRENHEIT_UNIT_API)){
            tempUnitText.text = FAHRENHEIT_UNIT_TEXT
        } else {
            tempUnitText.text = CELSIUS_UNIT_TEXT
        }



    }

    override fun onLocationChanged(location: Location?) {
        lat = location?.latitude.toString()
        lon = location?.longitude.toString()
        getCurrentData()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
         Toast.makeText(applicationContext, getString(R.string.enable_gps_and_internet), Toast.LENGTH_SHORT).show()
    }


}
