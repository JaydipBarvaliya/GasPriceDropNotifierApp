package com.gpn.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpn.data.GasStation
import com.gpn.network.FindByCityOrZipcodeResponse
import com.gpn.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await

class GasPriceViewModel : ViewModel() {

    val stationsState = mutableStateOf<List<GasStation>>(emptyList())

    fun fetchStations(search: String, fuel: Int, maxAge: Int, brandId: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: FindByCityOrZipcodeResponse =
                    RetrofitClient.gasPriceApi.findByCityOrZipcode(search, fuel, maxAge, brandId).await()

                val stations = response.data
                    ?.locationBySearchTerm
                    ?.stations
                    ?.results ?: emptyList()

                // Update UI state on the main thread
                viewModelScope.launch(Dispatchers.Main) {
                    stationsState.value = stations
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
