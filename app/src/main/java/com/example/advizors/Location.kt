package com.example.advizors

import java.io.Serializable

import com.google.gson.annotations.SerializedName


data class Location(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("city") val city: String,
    @SerializedName("street") val street : String
): Serializable

data class Properties (
    @SerializedName("properties") val properties: Location,
): Serializable

data class LocationResponse(
    @SerializedName("type") val type: String,
    @SerializedName("features") val features: Array<Properties>,
): Serializable

