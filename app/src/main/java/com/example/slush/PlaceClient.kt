package com.example.slush

import android.content.Context
import android.provider.Settings.Global.getString
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams


class PlaceClient {
    private var client: AsyncHttpClient? = null
    private val API_BASE_URL: String = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json"
    private val API_ORGANIZATIONS_URL: String = "/organizations"
    var accessToken: String = "@string"

    init {
        client = AsyncHttpClient()
    }

    fun getPlaces(handler: JsonHttpResponseHandler, lat: Double, lng: Double, c: Context){
        val url = API_BASE_URL
        print(url)
        val params = RequestParams()
        val key: String = c.getString(R.string.google_maps_key)
        var locationString: String = "$lat,$lng"
        params.put("location", locationString)
        params.put("radius", "5000")
        params.put("types", "gas_station")
        params.put("key", key)
        client?.get(url,params,handler)
    }

    fun getApiUrl(relativeUrl: String): String{
        return API_BASE_URL.plus(relativeUrl)
    }

}