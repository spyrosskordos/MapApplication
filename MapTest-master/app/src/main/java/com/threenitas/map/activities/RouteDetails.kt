package com.threenitas.map.activities

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.threenitas.map.R
import com.threenitas.map.api.APIClient
import com.threenitas.map.communicator.Communicator
import com.threenitas.map.models.DirectionResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RouteDetails : AppCompatActivity() {
    private val model by lazy {
        ViewModelProviders.of(this).get(Communicator::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_details)
        val texttime = findViewById(R.id.item_detailtime) as TextView
        val distance = findViewById(R.id.item_detaildistance) as TextView
        try {
            texttime.text = "Total Time:" + intent.getStringExtra("time")
            distance.text = "Distance Time" + intent.getStringExtra("distance")
        } catch (e: Exception) {
            Log.d("OnResponse", "thereis an error")
            e.printStackTrace()
        }

    }


}
