package com.example.grabapp.data.repository

import android.Manifest
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.grabapp.R
import com.example.grabapp.data.api.MapApi
import com.example.grabapp.data.model.order.AddressInfo
import com.example.grabapp.data.model.order.PriceRouteItem
import com.example.grabapp.network.RetrofitInstance
import com.example.grabapp.respone.AutoCompleteResponse
import com.example.grabapp.respone.Coordinates
import com.example.grabapp.respone.GeocodeResponse
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.respone.PlaceDetailResponse
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddressRepository(context: Context) {
    private val apiService =
        RetrofitInstance.getInstance(context.getString(R.string.goong_api_url))
            .create(MapApi::class.java)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): android.location.Location? {
        var location = fusedLocationClient.lastLocation.await()
        if (location == null) {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).build()
            location = suspendCoroutine { cont ->
                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        fusedLocationClient.removeLocationUpdates(this)
                        cont.resume(result.lastLocation)
                    }
                }
                fusedLocationClient.requestLocationUpdates(
                    request,
                    callback,
                    Looper.getMainLooper()
                )
            }
        }
        return location
    }

    fun getDetailAddressById(
        input: String,
        onSuccess: (PlaceDetailResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val call = apiService.getDetailAddress(input, API_KEY)
        call.enqueue(object : Callback<PlaceDetailResponse> {
            override fun onResponse(
                call: Call<PlaceDetailResponse?>,
                response: Response<PlaceDetailResponse?>
            ) {
                if (response.isSuccessful) {
                    Log.d("grabtest", response.body()?.result?.name ?: "")
                    response.body()?.let {
                        onSuccess(it)
                    }
                }
            }

            override fun onFailure(
                call: Call<PlaceDetailResponse?>,
                t: Throwable
            ) {
                onError(t)
            }

        })
    }

    fun getAddressByCoordinates(
        input: Coordinates,
        onSuccess: (GeocodeResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val call = apiService.getAddressByCoordinates(input.toString(), API_KEY)
        call.enqueue(object : Callback<GeocodeResponse> {
            override fun onResponse(
                call: Call<GeocodeResponse?>,
                response: Response<GeocodeResponse?>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    }
                }
            }

            override fun onFailure(
                call: Call<GeocodeResponse?>,
                t: Throwable
            ) {
                onError(t)
            }

        })
    }

    fun searchAddress(
        input: String,
        onSuccess: (AutoCompleteResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val call = apiService.getAutoComplete(input, API_KEY, LIMIT)
        call.enqueue(object : Callback<AutoCompleteResponse> {
            override fun onResponse(
                call: Call<AutoCompleteResponse>,
                response: Response<AutoCompleteResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError(Throwable("Error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<AutoCompleteResponse>, t: Throwable) {
                onError(t)
            }
        })
    }

    suspend fun getDirectionData(
        pickUpAddress: AddressInfo,
        listData: List<PriceRouteItem>
    ): List<GoongDirectionApiResponse> {

        val result = mutableListOf<GoongDirectionApiResponse>()
        var origin = "${pickUpAddress.latitude},${pickUpAddress.longitude}"

        for (item in listData) {
            val destination = "${item.latitude},${item.longitude}"

            Log.d(
                "GoongAPI",
                "Request direction routeIndex=${item.routeIndex}: origin=$origin dest=$destination"
            )

            val response = apiService.getDirections(origin, destination, "bike", API_KEY)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(
                        "GoongAPI",
                        "Direction success routeIndex=${item.routeIndex}: ${body.routes?.size ?: 0} routes"
                    )
                    result.add(body)
                    origin = destination
                } else {
                    Log.e("GoongAPI", "Body null routeIndex=${item.routeIndex}")
                }
            } else {
                Log.e(
                    "GoongAPI",
                    "Error routeIndex=${item.routeIndex}: ${response.code()} ${response.message()}"
                )
            }
        }

        return result
    }

    companion object {
        const val API_KEY = "QZOL9kM8PSx8NxcCENLSgc4g9n64iXggSRhhKSXA"
        const val MAP_KEY = "2HeIX1FS3q6CNg4XNcvMH5oxAD0MbSm9Y4SRUnb7"
        const val LIMIT = "10"
        private lateinit var addressRepository: AddressRepository
        fun getInstance(context: Context): AddressRepository {
            if (!::addressRepository.isInitialized)
                addressRepository = AddressRepository(context)
            return addressRepository
        }
    }
}