package com.aashutosh.desimall_pro.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aashutosh.desimall_pro.api.LocationAPI
import com.aashutosh.desimall_pro.models.NominatimResponse
import com.aashutosh.desimall_pro.sealed.StateResponse
import retrofit2.Response
import javax.inject.Inject

/*class LocationRepository @Inject constructor(
    private val locationService: LocationAPI
) {
    private val locationLiveData = MutableLiveData<StateResponse<NominatimResponse>>()

    val locationResponse: LiveData<StateResponse<NominatimResponse>>
        get() = locationLiveData

    suspend fun reverseGeocode(latitude: Double, longitude: Double) {
        try {
            locationLiveData.postValue(StateResponse.Loading())
            val result = locationService.reverseGeocode(latitude, longitude)
            if (result.isSuccessful) {
                val body = result.body()
                if (body != null) {
                    locationLiveData.postValue(StateResponse.Success(body))
                } else {
                    locationLiveData.postValue(StateResponse.Error("Empty response body"))
                }
            } else {
                locationLiveData.postValue(StateResponse.Error("Error in API: ${result.code()}"))
            }
        } catch (e: Exception) {
            locationLiveData.postValue(StateResponse.Error(e.toString()))
        }
    }
}*/
class LocationRepository @Inject constructor(private val locationAPI: LocationAPI) {

    suspend fun reverseGeocode(latitude: Double, longitude: Double): Response<NominatimResponse> {
        return locationAPI.reverseGeocode(latitude, longitude)
    }
}

