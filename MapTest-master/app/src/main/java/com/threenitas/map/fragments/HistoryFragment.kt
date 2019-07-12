package com.threenitas.map.fragments


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import com.google.android.gms.maps.model.LatLng
import com.threenitas.map.R
import com.threenitas.map.activities.MyApplication
import com.threenitas.map.adapters.UserSearchAdapter
import com.threenitas.map.models.UserSearch
import io.realm.Realm
import com.threenitas.map.fragments.MapFragment

import com.threenitas.map.communicator.Communicator


/**
 * A simple [Fragment] subclass.
 *
 */
class HistoryFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var title:TextView

    private val model by lazy {
        ViewModelProviders.of(activity).get(Communicator::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view!!.findViewById(R.id.recyclerView) as RecyclerView

        //title.text="Search History"

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        val toolbar =view!!.findViewById(R.id.toolbar) as  android.support.v7.widget.Toolbar?

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        toolbar?.title = "History"
        toolbar?.subtitle = "Sub"

        getRealmData()

    }


    private fun itemClicked(item: UserSearch) {
        val mapFragment: MapFragment = MapFragment()
        model.destination = LatLng(item.latitiude!!, item.longitude!!)


        val manager = fragmentManager

        model.calledFromHistory = true

        manager
            .beginTransaction()
            .replace(R.id.fragment_container, mapFragment).addToBackStack(null).commit()

    }


    fun getRealmData() {
        var realmdb: Realm? = null
        realmdb = Realm.getDefaultInstance()
        var allSearches = realmdb!!.where(UserSearch::class.java).findAll()

        val adapter = UserSearchAdapter(allSearches, { item: UserSearch -> itemClicked(item) })
        recyclerView.adapter = adapter
    }
}
