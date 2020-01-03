package com.example.slush
import org.json.JSONArray
import org.json.JSONObject

class PlaceModel {
    var id: String? = null
    var name:String? = null
    var lat: Double = 0.0
    var lng: Double = 0.0

    companion object {
        fun fromJSON(jsonObject: JSONObject): PlaceModel{
            val placeModel = PlaceModel()
            println("HERE" + jsonObject)
            placeModel.id = jsonObject.getString("place_id")
            placeModel.name = jsonObject.getString("name")
            val geo: JSONObject = jsonObject.getJSONObject("geometry")
            println("GEO: " + geo)
            val loc: JSONObject = geo.getJSONObject("location")
            println("LOC: " + loc)
            placeModel.lat = loc.getDouble("lat")
            placeModel.lng = loc.getDouble("lng")

            //TODO: Remove
            print("*****\n*\n")
            print(placeModel.name + "  " + placeModel.id)
            print("*****\n*\n")

            return placeModel
        }

        fun fromJSON(jsonArray: JSONArray): ArrayList<PlaceModel>{
            val places = ArrayList<PlaceModel>()

            for (i in 0 until (jsonArray.length() - 1)) {
                places.add(fromJSON(jsonArray.getJSONObject(i)))
            }

            return places
        }
    }
}