package com.application.jessiejay.weatherapplicationv101

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity: AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        }
        toolbar.setTitle(getString(R.string.settings_toolbar))
        toolbar.setNavigationOnClickListener{
            finish()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

//        drawerLayout = findViewById(R.id.drawer_layout)
//        navigationView = findViewById(R.id.nav_view)
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            menuItem.isChecked = true
//            drawerLayout.closeDrawers()
//            when(menuItem.itemId){
//                R.id.drawer_logout ->{
//                    firebaseAuth.signOut()
//                    finish()
//                }
//                R.id.drawer_settings ->{
//
//                }
//            }
//            true
//        }
    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        return when (item?.itemId) {
//            android.R.id.home -> {
////                drawerLayout.openDrawer(GravityCompat.START)
////                true
//                finish()
//                val intent = Intent(applicationContext,)
//                startActivity()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}