package com.example.slush
import org.json.JSONArray
import org.json.JSONObject

class PlaceModel {
    var id: String? = null
    var name:String? = null
    var location: String? = null
    var email: String? = null

    companion object {
        fun fromJSON(jsonObject: JSONObject): PlaceModel{
            val placeModel = PlaceModel()
            placeModel.id = jsonObject.getString("id")
            placeModel.name = jsonObject.getString("name")
            placeModel.location = jsonObject.getString("address")
            placeModel.email = jsonObject.getString("email")

            //TODO: Remove
            print("*****\n*\n")
            print(placeModel.name + "  " + placeModel.location)
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