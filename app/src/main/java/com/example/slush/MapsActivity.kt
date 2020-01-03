package com.example.slush

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showLocation()
        fetchPlaces()

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
    }

    fun fetchPlaces() {
        val client = PlaceClient()
        client.getPlaces(object: JsonHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                val items = response?.getJSONArray("results")
                println(items)
                if (items != null) {
                    val places = PlaceModel.fromJSON(items)
                    println(places)
                    val addressList = mutableListOf<Address>()
                    Log.i("Assert", places.toString())
                    for(place in places) {
                        val locationName = place.name
                        val gc = Geocoder(applicationContext)
                        addressList += gc.getFromLocationName(locationName, 5)
                        Log.e("Information", addressList.toString())
                    }
                    var latLngs = arrayListOf<LatLng>()
                    for(place in places) {
                        var latlng = LatLng(place.lat, place.lng)
                        var placeMarker: Marker = mMap.addMarker(MarkerOptions()
                                                        .position(latlng)
                                                        .title(place.name)
                                                        .snippet(place.address))
                        placeMarker.showInfoWindow()
                        latLngs.add(latlng)
                    }
                    var builder = LatLngBounds.Builder();
                    for(pos in latLngs) {
                        builder.include(pos)
                    }
                    var bounds: LatLngBounds = builder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80))
                }
                Toast.makeText(applicationContext, items?.length().toString()
                    .plus(" items loaded"), Toast.LENGTH_LONG).show()
                print(items)
                super.onSuccess(statusCode, headers, response)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseString: String?,
                throwable: Throwable?
            ) {
                Toast.makeText(applicationContext,responseString, Toast.LENGTH_LONG).show()
                super.onFailure(statusCode, headers, responseString, throwable)
            }
        }, getLat(), getLng(), applicationContext)
    }

    private fun doShowLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            moveCam()
        }
    }

    @SuppressLint("MissingPermission")
    fun getLat(): Double {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val location: Location =
            locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))

        val lat: Double = location.getLatitude()
        return lat
    }

    @SuppressLint("MissingPermission")
    fun getLng(): Double {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val location: Location =
            locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))

        val lng: Double = location.getLongitude()
        return lng
    }

    private fun moveCam() {
        var lat = getLat()
        var lng = getLng()
        var currentPosition = LatLng(lat, lng)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
    }

    private fun showLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101)
        } else {
            doShowLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doShowLocation()
        }
    }
}
