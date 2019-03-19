package com.application.jessiejay.weatherapplicationv101;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather?")
    Call<WeatherResponse> getCurrentWeatherInfo (
            @Query("lat") String latitude,
            @Query("lon") String longitude,
            @Query("appid") String appid,
            @Query("units") String unit);

}
