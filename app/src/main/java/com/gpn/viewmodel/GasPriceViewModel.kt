package com.gpn.viewmodel

import GasStation
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpn.network.GasPriceApi
import com.gpn.network.PriceAlertRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Data Models
data class BrandResponse(val data: BrandData?)
data class BrandData(val brands: List<Brand>?)
data class Brand(val brandId: String, val name: String)

@HiltViewModel
class GasPriceViewModel @Inject constructor(private val gasPriceApi: GasPriceApi) : ViewModel() {

    // -------------------------------------------
    // 🚀 1️⃣ BRAND MANAGEMENT
    // -------------------------------------------
    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands.asStateFlow()
    private val _selectedBrand = MutableStateFlow<Brand?>(null)

    init {
        fetchBrands()
    }

    fun setSelectedBrand(brand: Brand) {
        _selectedBrand.value = brand
    }

    private fun fetchBrands() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val brandList = gasPriceApi.getBrands().data?.brands.orEmpty()

                if (brandList.isEmpty()) {
                    Log.e("GasPriceViewModel", "Brand list is empty!")
                }

                _brands.value = brandList
            } catch (e: Exception) {
                Log.e("GasPriceViewModel", "Error fetching brands: ${e.localizedMessage}", e)
            }
        }
    }

    // -------------------------------------------
    // ⛽ 2️⃣ STATION FETCHING
    // -------------------------------------------
    private val _stationsState = MutableStateFlow<List<GasStation>>(emptyList())
    val stationsState: StateFlow<List<GasStation>> = _stationsState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    private val fuelTypeMap = mapOf(
        "Regular" to 1, "Midgrade" to 2, "Premium" to 3, "Diesel" to 4, "E85" to 5, "UNL88" to 12
    )

    fun getFuelTypeId(fuelTypeName: String): Int {
        return fuelTypeMap[fuelTypeName] ?: 1
    }

    fun fetchStations(search: String, fuel: Int, maxAge: Int, brandId: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = gasPriceApi.findByCityOrZipcode(search, fuel, maxAge, brandId)

                val stations = response.data?.locationBySearchTerm?.stations?.results.orEmpty()
                val countryCode = response.data?.locationBySearchTerm?.countryCode ?: "Unknown"

                val updatedStations = stations.map { station ->
                    station.copy(address = station.address.copy(country = countryCode))
                }

                withContext(Dispatchers.Main) {
                    _stationsState.value = updatedStations
                    _errorState.value = null
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorState.value = "Failed to fetch stations: ${e.localizedMessage}"
                }
            }
        }
    }

    // -------------------------------------------
    // ⚠️ 3️⃣ ALERT MANAGEMENT
    // -------------------------------------------
    private val _selectedStation = MutableStateFlow<GasStation?>(null)
    val selectedStation: StateFlow<GasStation?> = _selectedStation.asStateFlow()

    var isDialogOpen = mutableStateOf(false)
    var selectedFuelType = mutableStateOf("Regular")
    var expectedPrice = mutableFloatStateOf(0.0f)

    fun showCreateAlertDialog(station: GasStation) {
        _selectedStation.value = station
        isDialogOpen.value = true
    }

    fun closeDialog() {
        isDialogOpen.value = false
        expectedPrice.floatValue = 0.0f
        selectedFuelType.value = "Regular"
        _selectedStation.value = null
    }

    fun createPriceAlert() {

        val station = _selectedStation.value ?: return
        val price = expectedPrice.floatValue
        val fuelType = selectedFuelType.value

        if (price <= 0.0f) {
            println("🚨 Error: Please enter a valid price")
            return
        }

        viewModelScope.launch {
            try {
                val response = gasPriceApi.createPriceAlert(
                    PriceAlertRequest(
                        station.id, getFuelTypeId(fuelType), price,
                        station.address.line1,
                        station.address.locality,
                        station.address.postalCode,
                        station.address.region,
                        station.name
                    )
                )
            println("✅ API Response: $response")
            }catch (e: Exception){
                println("🚨 API Error: ${e.message}")
            }
        }
        println("Alert created for ${_selectedStation.value?.name} at ${expectedPrice.floatValue} for ${selectedFuelType.value}")
        closeDialog()
    }

}
