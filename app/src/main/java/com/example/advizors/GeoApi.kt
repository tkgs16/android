package com.example.advizors

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApi {

    @GET("geocode/reverse?apiKey=a83434a585f04621b8ee222d9a1970fd")
    fun getLocation(
        @Query("lon") lon: Double,
        @Query("lat") latitude: Double
    ): Call<LocationResponse>
}
