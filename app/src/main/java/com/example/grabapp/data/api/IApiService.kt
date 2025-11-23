package com.example.grabapp.data.api

import com.example.grabapp.respone.AutoCompleteResponse
import com.example.grabapp.respone.GeocodeResponse
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.respone.PlaceDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface IApiService {
    @GET("v2/place/autocomplete")
    fun getAutoComplete(
        @Query("input") input: String?,
        @Query("api_key") apikey: String?,
        @Query("limit") limit:String?
    ): Call<AutoCompleteResponse>

    @GET("v2/geocode")
    fun getAddressByCoordinates(
        @Query ("latlng") coordinates:String,
        @Query ("api_key") apikey: String?
    ): Call<GeocodeResponse>

    @GET("place/detail")
    fun getDetailAddress(
        @Query ("place_id") coordinates:String,
        @Query ("api_key") apikey: String?
    ): Call<PlaceDetailResponse>

    @GET("Direction")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("vehicle") vehicle: String = "car",
        @Query("api_key") apiKey: String
    ): Call<GoongDirectionApiResponse>
}