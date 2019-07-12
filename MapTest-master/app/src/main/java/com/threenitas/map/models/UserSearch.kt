package com.threenitas.map.models

import com.google.android.gms.maps.model.LatLng
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class UserSearch() : RealmObject(){
    var latitiude:Double?=null
    var longitude:Double?=null
    var description:String?=null

}