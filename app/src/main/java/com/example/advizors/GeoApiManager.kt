package com.example.advizors

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
class GeoApiManager {

    private val restGeoApi = "https://api.geoapify.com/v1/"

    private val api = Retrofit.Builder().baseUrl(restGeoApi)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create()).build().create(GeoApi::class.java)

    fun getLocation(longitude : Double, latitude: Double): Call<LocationResponse> {
        return api.getLocation(longitude, latitude)
    }
}