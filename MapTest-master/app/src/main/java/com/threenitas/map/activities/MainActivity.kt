package com.threenitas.map.activities


import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.threenitas.map.fragments.HistoryFragment
import com.threenitas.map.R
import com.threenitas.map.fragments.MapFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.ActivityCompat

import com.threenitas.map.communicator.Communicator
import com.threenitas.map.models.UserSearch

class MainActivity : AppCompatActivity() {

    private val mapFragment: MapFragment
    private val historyFragment: HistoryFragment
    private val model by lazy {
        ViewModelProviders.of(this).get(Communicator::class.java)
    }


    init {
        mapFragment = MapFragment()
        historyFragment = HistoryFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Configure action bar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        // Initialize the action bar drawer toggle instance
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                //toast("Drawer closed")
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                //toast("Drawer opened")
            }
        }


        // Configure the drawer layout to add listener and show icon on toolbar
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()


        // Set navigation view navigation item selected listener
        navigation_view.setNavigationItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)

            when (it.itemId) {

                R.id.item_map -> {
                    model.calledFromHistory = false
                    transaction.replace(R.id.fragment_container, mapFragment)

                }


                R.id.item_history -> transaction.replace(R.id.fragment_container, historyFragment)


            }
            transaction.addToBackStack(null)
            transaction.commit()
            // Close the drawer
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // Extension function to show toast message easily
    private fun Context.toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}