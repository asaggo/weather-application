package com.application.jessiejay.weatherapplicationv101

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.MediaButtonReceiver.handleIntent
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader


class FindPlaceActivity : AppCompatActivity() {

    private val REQUEST_CODE = 200
    private lateinit var search_edittext: EditText
    private lateinit var search_button: Button
    private lateinit var result_listview: ListView
    private lateinit var location_list : MutableList<Any>
    private lateinit var coordinate_list : MutableList<Any> //this should only have one item on the list
    private lateinit var adapter: ArrayAdapter<Any>

    private lateinit var locationInfo: MutableList<String>


    private lateinit var cityListDatabaseHelper: CityListDatabaseHelper
    val TAG = "FindPlace"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_place)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        }

        cityListDatabaseHelper = CityListDatabaseHelper(this)


        val inputStream = resources.openRawResource(R.raw.worldcities)
        val buffer = BufferedReader(InputStreamReader(inputStream,"UTF-8"))
        var line = buffer.readLine()

        while (line != null) {
            val str = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var isInserted = cityListDatabaseHelper.insertData(str[0].toInt(),str[1],str[2].toDouble(),str[3].toDouble(),str[4],str[5])
            if(!isInserted){
                Log.i(TAG,"not inserted")
            }
            line = buffer.readLine()
        }
        
        search_edittext = findViewById(R.id.search_edittext)
        search_button = findViewById(R.id.search_button)
        result_listview = findViewById(R.id.result_listview)
        location_list = ArrayList()
        
        search_button.setOnClickListener {
            location_list.clear()
            val cursor = cityListDatabaseHelper.searchData(search_edittext.text.toString())
            if(cursor.count == 0) {
                Toast.makeText(this,"No result",Toast.LENGTH_SHORT).show()
            } else {
                while (cursor.moveToNext()) {
                    location_list.add(
                                    cursor.getString(1)+", "+
                                    cursor.getString(5)+", "+
                                    cursor.getString(4))
                }
                adapter = ArrayAdapter(this,R.layout.list_row,location_list)
                result_listview.adapter = adapter
            }
        }

        result_listview.setOnItemLongClickListener{ parent, view, position, id ->
            val selectedItem = result_listview.getItemAtPosition(position).toString()
            val result_list = selectedItem.split(",")
            locationInfo = ArrayList()
            for(str in result_list){
                locationInfo.add(str.trim())
            }

            val cursor = cityListDatabaseHelper.findCoordinates(locationInfo[0],locationInfo[1],locationInfo[2])
//            coordinate_list = ArrayList()
            val intent = Intent()
            if(cursor.count == 0) {
                Toast.makeText(this,"Error finding coordinates for the city",Toast.LENGTH_SHORT).show()
            } else {
                while (cursor.moveToNext()) {
                    intent.putExtra("latitude",cursor.getString(0))
                    intent.putExtra("longitude",cursor.getString(1))
                }
            }
            setResult(Activity.RESULT_OK,intent)
            finish()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                val intent = Intent()
                setResult(Activity.RESULT_CANCELED,intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
