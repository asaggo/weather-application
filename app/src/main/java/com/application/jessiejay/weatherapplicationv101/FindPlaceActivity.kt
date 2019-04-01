package com.application.jessiejay.weatherapplicationv101

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        val str1 = "INSERT INTO $tableName ($columns) values("
        val str2 = ");"


//        db.beginTransaction()
        while (line != null) {
            val sb = StringBuilder(str1)
            val str = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            sb.append("'" + str[0] + "',")
            sb.append(str[1] + "',")
            sb.append(str[2] + "',")
            sb.append(str[3] + "'")
            sb.append(str[4] + "'")
            sb.append(str2)
//            db.execSQL(sb.toString())
            line = buffer.readLine()
        }
//        db.setTransactionSuccessful()
//        db.endTransaction()
    }
}
