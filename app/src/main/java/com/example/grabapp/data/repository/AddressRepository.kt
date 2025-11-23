package com.example.grabapp.data.repository

import android.Manifest
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.grabapp.R
import com.example.grabapp.data.api.IApiService
import com.example.grabapp.network.RetrofitInstance
import com.example.grabapp.domain.model.location.Address
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
            .create(IApiService::class.java)
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

    fun getDirectionData(
        dropOff: Address,
        pickUp: Address,
        onSuccess: (GoongDirectionApiResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val origin = "${pickUp.coordinates.lat},${pickUp.coordinates.lng}"
        val destination = "${dropOff.coordinates.lat},${dropOff.coordinates.lng}"

        Log.d("GoongAPI", "Requesting direction: origin=$origin dest=$destination")

        val call = apiService.getDirections(origin, destination, "bike", API_KEY)
        call.enqueue(object : Callback<GoongDirectionApiResponse> {
            override fun onResponse(
                call: Call<GoongDirectionApiResponse>,
                response: Response<GoongDirectionApiResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("GoongAPI", "Direction success: ${body.routes?.size ?: 0} routes found")
                        onSuccess(body)
                    } else {
                        Log.e("GoongAPI", "Response body is null")
                        onError("Dữ liệu trả về rỗng")
                    }
                } else {
                    Log.e("GoongAPI", "Error response: ${response.code()} ${response.message()}")
                    onError("Lỗi server: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GoongDirectionApiResponse>, t: Throwable) {
                Log.e("GoongAPI", "Network error: ${t.message}", t)
                onError("Lỗi mạng: ${t.localizedMessage}")
            }
        })
    }


    companion object {
        const val API_KEY = "lFHqJVvz4R97UeVo202sBed6FGh7KSi5CJZvjacg"
        const val MAP_KEY = "ZxCQP01JWowkSbE5ELrpGNvEJTD3vHYILu3uyvlx"
        const val LIMIT = "10"
        private lateinit var addressRepository: AddressRepository
        fun getInstance(context: Context): AddressRepository {
            if (!::addressRepository.isInitialized)
                addressRepository = AddressRepository(context)
            return addressRepository
        }
    }
}