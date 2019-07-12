package com.threenitas.map.communicator


import android.arch.lifecycle.ViewModel;
import com.google.android.gms.maps.model.LatLng

class Communicator: ViewModel(){
    var calledFromHistory=false
    lateinit var origin:LatLng
    var destination:LatLng=LatLng(0.0000000,0.0000000)
   lateinit var distance:String
    lateinit var time:String
}