package com.threenitas.map.interfaces

import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

import com.threenitas.map.models.DirectionResults
import com.threenitas.map.models.Predictions


interface GoogleInterface {
    @GET("/maps/api/directions/json?key=AIzaSyBYNiTqYpnmG_arzSYEJIVktlYSGtUDtL4")
    fun getDirectionBetween(

        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode :String
        ): Call<DirectionResults>
    @GET("place/autocomplete/json")
    fun getPlacesAutoComplete(
        @Query("input") input: String,
        @Query("types") types: String,
        @Query("language") language: String,
        @Query("key") key: String
    ): Call<Predictions>
}