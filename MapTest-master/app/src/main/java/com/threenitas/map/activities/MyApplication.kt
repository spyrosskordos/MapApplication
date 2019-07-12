package com.threenitas.map.activities

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.threenitas.map.models.UserSearch
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    var realmDB: Realm? = null


    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        
        val configuration = RealmConfiguration.Builder().name("usersearchs.realm").build()
        realmDB = Realm.getInstance(configuration)
        Realm.setDefaultConfiguration(configuration)
        Log.d("rea", "asdfghj")

    }
}