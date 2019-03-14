package com.application.jessiejay.weatherapplicationv101;

import android.arch.lifecycle.ViewModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeatherViewModel extends ViewModel {
    public String cityName;
    public Clouds clouds;
    public float temp;
    public float humidity;
    public float pressure;
    public float temp_min;
    public float temp_max;
    public float windSpeed;
    public String countryName;
    public Coord coord;
    public int id;
    public Date currentTime;
    public String currentTime_str;

    public void setCurrentTime(){
        currentTime = Calendar.getInstance().getTime();
        currentTime_str = DateFormat.getDateInstance().format(currentTime);
    }

    public void setValues(WeatherResponse weatherResponse) {
        this.cityName = weatherResponse.name;
        this.clouds = weatherResponse.clouds;
        this.coord = weatherResponse.coord;
        this.countryName = weatherResponse.sys.country;
        this.humidity = weatherResponse.main.humidity;
        this.id = weatherResponse.id;
        this.temp = weatherResponse.main.temp;
        this.temp_min = weatherResponse.main.temp_min;
        this.temp_max = weatherResponse.main.temp_max;
        this.windSpeed = weatherResponse.wind.speed;
    }
}
