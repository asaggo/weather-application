package com.application.jessiejay.weatherapplicationv101

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader


class FindPlaceActivity : AppCompatActivity() {

    private lateinit var cityListDatabaseHelper: CityListDatabaseHelper
    val TAG = "FindPlace"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_place)

        cityListDatabaseHelper = CityListDatabaseHelper(this)

        val inputStream = resources.openRawResource(R.raw.worldcities)
        val buffer = BufferedReader(InputStreamReader(inputStream,"UTF-8"))
        var line = buffer.readLine()
        val tableName = "locations_table"
        val columns = "city, latitude, longitude, country, admin_name"
//        val str1 = "INSERT INTO $tableName ($columns) values("
//        val str2 = ");"

//        db.beginTransaction()
        while (line != null) {
            val str = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var isInserted = cityListDatabaseHelper.insertData(str[0],str[1].toDouble(),str[2].toDouble(),str[3],str[4])
            if(!isInserted){
                Log.i(TAG,"not inserted")
            }
            line = buffer.readLine()
        }
//        db.setTransactionSuccessful()
//        db.endTransaction()
    }
}
