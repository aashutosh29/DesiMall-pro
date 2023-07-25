package com.aashutosh.desimall_pro.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aashutosh.desimall_pro.models.NominatimResponse
import com.aashutosh.desimall_pro.repository.LocationRepository
import com.aashutosh.desimall_pro.sealed.StateResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val locationRepository: LocationRepository) : ViewModel() {

    private val _locationResponse = MutableLiveData<StateResponse<NominatimResponse>>()
    val locationResponse: LiveData<StateResponse<NominatimResponse>>
        get() = _locationResponse

    // Function to fetch location data
    fun fetchLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                _locationResponse.value = StateResponse.Loading()
                val response = locationRepository.reverseGeocode(latitude, longitude)
                if (response.isSuccessful) {
                    _locationResponse.value = StateResponse.Success(response.body())
                } else {
                    _locationResponse.value = StateResponse.Error("Error in API")
                }
            } catch (e: Exception) {
                _locationResponse.value = StateResponse.Error(e.toString())
            }
        }
    }
}

