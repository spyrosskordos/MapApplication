package com.threenitas.map.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.threenitas.map.R
import android.os.Build
import com.threenitas.map.models.Prediction
import com.google.maps.android.PolyUtil;
import android.util.Log
import com.google.android.gms.maps.*
import com.threenitas.map.adapters.PlacesAutoCompleteAdapter
import kotlinx.android.synthetic.*
import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.*
import com.threenitas.map.activities.MyApplication
import com.threenitas.map.activities.RouteDetails

import com.threenitas.map.api.APIClient
import com.threenitas.map.communicator.Communicator
import java.io.IOException
import com.threenitas.map.interfaces.GoogleInterface
import com.threenitas.map.models.DirectionResults
import com.threenitas.map.models.UserSearch
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MapFragment : Fragment(), OnMapReadyCallback,
    OnMarkerClickListener {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var googleMap: GoogleMap
    lateinit var mapView: MapView
    lateinit var autoCompleteTextViewPlace: AutoCompleteTextView

    lateinit var detailsButton: Button

    var marker: Marker? = null
    var userHasSearched: Boolean? = false

    private val model by lazy {
        ViewModelProviders.of(activity).get(Communicator::class.java)
    }

    companion object {
        private val MY_PERMISSION_FINE_LOCATION = 101
    }

    override fun onMapReady(p0: GoogleMap?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_map, container, false)

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        autoCompleteTextViewPlace = view.findViewById(R.id.autoCompleteTextViewPlace) as AutoCompleteTextView
        detailsButton = view.findViewById(R.id.detailsButton) as Button
        detailsButton.visibility = View.GONE
        mapView = view.findViewById<MapView>(R.id.map_view) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(OnMapReadyCallback {
            googleMap = it
            if (model.calledFromHistory == true) {
                mapView.onResume()
                getLocation()

                placeMarkerOnMap(model.destination, false)

             //   loadAutocompleteData()


            } else {

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    getLocation()
                } else {//condition for Marshmello and above
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSION_FINE_LOCATION
                        )
                    }
                }



                Log.d("GoogleMap", "before isMyLocationEnabled")

                mapView.onResume()
                loadAutocompleteData()
            }

        })


    }


    private fun buttonActivity() {
        val slideUp:Animation=AnimationUtils.loadAnimation(context,R.anim.slide_up)
        detailsButton.visibility = View.VISIBLE
        detailsButton.startAnimation(slideUp)
        detailsButton.setOnClickListener {
            val intent = Intent(context, RouteDetails::class.java)
            try {
                intent.putExtra("time", model.time)
                intent.putExtra("distance", model.distance)
                startActivity(intent)
            }catch (e: Exception) {
                Toast.makeText(context, "This action requires valid api key", Toast.LENGTH_LONG)
                e.printStackTrace()
            }


        }
    }


    private fun saveSearch(description: String, lat: Double, long: Double) {

        var realmDB: Realm? = null
        realmDB = Realm.getDefaultInstance()
        realmDB!!.beginTransaction()

        val userSearch = realmDB.createObject(UserSearch::class.java)
        userSearch.description = description
        userSearch.latitiude = lat
        userSearch.longitude = long
        realmDB.commitTransaction()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//permission to access location grant
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    getLocation()
                }
            }
            //permission to access location denied
            else {
                Toast.makeText(context, "This app requires location permissions to be granted", Toast.LENGTH_LONG)
                    .show()

            }
        }
    }

    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }

    private fun placeMarkerOnMap(location: LatLng, userloc: Boolean) {
        val markerOptions = MarkerOptions().position(location)

        val titleStr = getAddress(location)
        markerOptions.title(titleStr)

        if (userloc == false) {

            marker?.remove()

            marker = googleMap.addMarker(markerOptions)
            marker?.showInfoWindow()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 11f))
            userHasSearched = true
        } else {
            googleMap.addMarker(markerOptions)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

        }
        view?.hideKeyboard()

    }


    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
            return
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mFusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            println("lklk")
            if (location != null) {


                model.origin = LatLng(location.latitude!!, location.longitude!!)
                placeMarkerOnMap(model.origin, true)
            if(model.calledFromHistory==true)
                getDirections()
            }
        }

    }

    private fun getDirections() {
        val apiClient = APIClient




        val test: Call<DirectionResults> = apiClient.getClient.getDirectionBetween(
            model.origin.latitude.toString() + "," + model.origin.longitude.toString(),
            model.destination.latitude.toString() + "," + model.destination.longitude.toString(),
            "driving"
        )

        test.enqueue(object : Callback<DirectionResults> {
            override fun onFailure(call: Call<DirectionResults>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<DirectionResults>, response: Response<DirectionResults>) {


                try {


                    val encodeString= response.body()?.routes?.get(0)?.overviewPolyLine!!.getPoints()

                    model.distance = response.body()?.routes?.get(0)?.legs?.get(0)?.distance!!.text
                    model.time = response.body()?.routes?.get(0)?.legs?.get(0)?.duration!!.text

                    var decoded: List<LatLng> = PolyUtil.decode(encodeString)


                    googleMap.addPolyline(PolylineOptions().addAll(decoded).width(12f).color(Color.BLUE))

                } catch (e: Exception) {
                    Log.d("OnResponse", "thereis an error")
                    e.printStackTrace()
                }
                val slideUp:Animation=AnimationUtils.loadAnimation(context,R.anim.disappear)
                autoCompleteTextViewPlace.visibility=View.GONE
                autoCompleteTextViewPlace.startAnimation(slideUp)
                buttonActivity()
            }


        })


    }

    private fun loadAutocompleteData() {
        val placesAutoCompleteAdapter: PlacesAutoCompleteAdapter
        val predictions: MutableList<Prediction> = ArrayList()
        placesAutoCompleteAdapter = PlacesAutoCompleteAdapter(activity.applicationContext, predictions)
// The minimum number of characters to type to show the drop down
        autoCompleteTextViewPlace.threshold = 1
        autoCompleteTextViewPlace.setAdapter(placesAutoCompleteAdapter)

        autoCompleteTextViewPlace.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val selectedItermm = parent.getItemAtPosition(position) as Prediction
            val description = selectedItermm.description
            val geoCoder = Geocoder(context)
            var addressList: List<Address>? = null
            try {
                addressList = geoCoder.getFromLocationName(description, 1)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            val address = addressList!![0]
            model.destination = LatLng(address.latitude, address.longitude)

            placeMarkerOnMap(model.destination, false)
            saveSearch(description, address.latitude, address.longitude)
            getDirections()


        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}// Required empty public constructor