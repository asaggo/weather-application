package com.application.jessiejay.weatherapplicationv101

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import android.location.LocationListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), LocationListener {
     companion object {
        val baseUrl = "http://api.openweathermap.org/"
        val appid = "319af09ba07aabb7b53de35862c73840"
        val lat = "40.370104"
        val lon = "-111.781541"
        val TAG = "MyActivity"
    }
    private lateinit var viewModel: WeatherViewModel
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        val database = DatabaseHelper(this)

        getCurrentData()
//        val textView = findViewById<TextView>(R.id.textview)
//        val submit_btn = findViewById<Button>(R.id.submit_btn)
//        submit_btn.setOnClickListener(View.OnClickListener { getCurrentData() })

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

    fun getCurrentData() {
        val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
        val service: WeatherService = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeatherInfo(lat,lon,appid)
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
        val cityNameTextView = findViewById<TextView>(R.id.city_name_textview)
        cityNameTextView.text = viewModel.cityName
        val currentDateTime = findViewById<TextView>(R.id.current_time_textview)
        currentDateTime.text = viewModel.currentTime_str
        val temp = findViewById<TextView>(R.id.temp_textview)
        temp.text = viewModel.temp.toString()

    }
    override fun onLocationChanged(location: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
